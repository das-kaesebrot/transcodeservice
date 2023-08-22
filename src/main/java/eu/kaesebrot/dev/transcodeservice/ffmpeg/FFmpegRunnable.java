package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import com.github.manevolent.ffmpeg4j.*;
import com.github.manevolent.ffmpeg4j.source.AudioSourceSubstream;
import com.github.manevolent.ffmpeg4j.source.VideoSourceSubstream;
import com.github.manevolent.ffmpeg4j.stream.output.FFmpegTargetStream;
import com.github.manevolent.ffmpeg4j.stream.source.FFmpegSourceStream;
import com.github.manevolent.ffmpeg4j.transcoder.Transcoder;
import eu.kaesebrot.dev.transcodeservice.constants.ETrackPresetType;
import eu.kaesebrot.dev.transcodeservice.models.AudioTrackPreset;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import eu.kaesebrot.dev.transcodeservice.models.VideoTrackPreset;

import java.io.File;
import java.util.HashMap;

public class FFmpegRunnable implements Runnable {
    private final TranscodeJob job;
    private FFmpegSourceStream sourceStream;
    private FFmpegTargetStream targetStream;

    public FFmpegRunnable(TranscodeJob job) {
        this.job = job;

        bootstrap();
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

            if (videoPreset.getWidth() != null && videoPreset.getWidth().intValue() != inputFormat.getWidth()) {
                width = videoPreset.getWidth().intValue();
            }
            if (videoPreset.getHeight() != null && videoPreset.getHeight().intValue() != inputFormat.getHeight()) {
                height = videoPreset.getHeight().intValue();
            }
            if (videoPreset.getFramerate() != null && (1D / videoPreset.getFramerate()) != inputFormat.getFramesPerSecond()) {
                frame_rate = 1D / videoPreset.getFramerate();
            }

            // TODO implement gamut, target color space
            target.registerVideoSubstream(videoPreset.getVideoCodecName(), width, height, frame_rate, new HashMap<>());
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
    public void run() {
        try {
            Transcoder.convert(sourceStream, targetStream, Double.MAX_VALUE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
