package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import com.github.manevolent.ffmpeg4j.*;
import com.github.manevolent.ffmpeg4j.source.AudioSourceSubstream;
import com.github.manevolent.ffmpeg4j.source.VideoSourceSubstream;
import com.github.manevolent.ffmpeg4j.stream.output.FFmpegTargetStream;
import com.github.manevolent.ffmpeg4j.stream.source.FFmpegSourceStream;
import com.github.manevolent.ffmpeg4j.transcoder.Transcoder;
import eu.kaesebrot.dev.transcodeservice.constants.ETrackPresetType;
import eu.kaesebrot.dev.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.dev.transcodeservice.models.AudioTrackPreset;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import eu.kaesebrot.dev.transcodeservice.models.VideoTrackPreset;
import eu.kaesebrot.dev.transcodeservice.utils.AVUtils;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class FFmpegCallable implements Callable<Void> {
    private final TranscodeJob job;
    private FFmpegSourceStream sourceStream;
    private FFmpegTargetStream targetStream;
    private Exception finalException;

    public FFmpegCallable(TranscodeJob job) {
        this.job = job;

        bootstrap();
    }


    /**
     * @return The job as submitted via the constructor. This may not represent the job in its current state.
     */
    public TranscodeJob getAsociatedJob() {
        return job;
    }

    private void bootstrap() {
        File inFile = new File(job.getInFile());
        File outFile = job.getOutFileName().toFile();

        TranscodePreset preset = job.getPreset();

        if (!inFile.isFile())
            throw new IllegalArgumentException("File does not exist at given path! Path: " + job.getInFile());

        try (FFmpegSourceStream source = FFmpegIO.openInput(inFile).open(AVUtils.getInputFormat(outFile.getName()));
             FFmpegTargetStream target = FFmpegIO.openOutput(outFile).open(preset.getMuxer())) {
            source.registerStreams();

            var videoStream = (VideoSourceSubstream) source.getSubstreams().stream().filter(s -> s.getMediaType() == MediaType.VIDEO).findFirst().orElseThrow();

            VideoTrackPreset videoPreset = (VideoTrackPreset) preset
                    .getTrackPresets().stream()
                    .filter(t -> t.getType().equals(ETrackPresetType.VIDEO))
                    .findFirst().orElseThrow();

            AudioTrackPreset audioPreset = (AudioTrackPreset) preset
                    .getTrackPresets().stream()
                    .filter(t -> t.getType().equals(ETrackPresetType.AUDIO))
                    .findFirst().orElseThrow();

            VideoFormat inputFormat = videoStream.getFormat();

            int width = inputFormat.getWidth();
            int height = inputFormat.getHeight();
            double frame_rate = inputFormat.getFramesPerSecond();

            if (videoPreset.getWidth() != null && videoPreset.getWidth() != width) {
                width = videoPreset.getWidth();
            }
            if (videoPreset.getHeight() != null && videoPreset.getHeight() != height) {
                height = videoPreset.getHeight();
            }
            if (videoPreset.getFramerate() != null && (1D / videoPreset.getFramerate()) != frame_rate) {
                frame_rate = 1D / videoPreset.getFramerate();
            }

            // TODO implement gamut, target color space
            var videoOpts = new HashMap<String, String>();

            target.registerVideoSubstream(videoPreset.getVideoCodecName(), width, height, frame_rate, videoOpts);
            // target.setPixelFormat(avutil.AV_PIX_FMT_RGB24);

            for (AudioSourceSubstream audioSubstream : source.getSubstreams().stream()
                    .filter(s -> s.getMediaType() == MediaType.AUDIO)
                    .map(AudioSourceSubstream.class::cast)
                    .toList()) {

                var inputAudioFormat = audioSubstream.getFormat();

                int samplerate = inputAudioFormat.getSampleRate();

                if (audioPreset.getAudioSampleRate() != null && audioPreset.getAudioSampleRate() != samplerate) {
                    samplerate = audioPreset.getAudioSampleRate();
                }

                target.registerAudioSubstream(audioPreset.getAudioCodecName(), samplerate, inputAudioFormat.getChannels(), inputAudioFormat.getChannelLayout(), new HashMap<>());
            }

            sourceStream = source;
            targetStream = target;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Void call() {
        try {
            Transcoder.convert(sourceStream, targetStream, Double.MAX_VALUE);
        } catch (Exception e) {
            finalException = e;
        }

        return null;
    }

    public boolean ranSuccessfully() {
        return finalException == null;
    }

    public Exception getFinalException() {
        return finalException;
    }
}
