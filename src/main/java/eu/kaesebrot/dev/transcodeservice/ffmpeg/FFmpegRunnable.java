package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import com.github.manevolent.ffmpeg4j.*;
import com.github.manevolent.ffmpeg4j.source.VideoSourceSubstream;
import com.github.manevolent.ffmpeg4j.stream.output.FFmpegTargetStream;
import com.github.manevolent.ffmpeg4j.stream.source.FFmpegSourceStream;
import com.github.manevolent.ffmpeg4j.transcoder.Transcoder;
import eu.kaesebrot.dev.transcodeservice.constants.ETrackPresetType;
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

            VideoFormat previousVideoFormat = videoStream.getFormat();

            int width = previousVideoFormat.getWidth();
            int height = previousVideoFormat.getHeight();
            double frame_rate = previousVideoFormat.getFramesPerSecond();

            if (videoPreset.getWidth() != null && videoPreset.getWidth().intValue() != previousVideoFormat.getWidth()) {
                width = videoPreset.getWidth().intValue();
            }
            if (videoPreset.getHeight() != null && videoPreset.getHeight().intValue() != previousVideoFormat.getHeight()) {
                height = videoPreset.getHeight().intValue();
            }
            if (videoPreset.getFramerate() != null && (1D / videoPreset.getFramerate()) != previousVideoFormat.getFramesPerSecond()) {
                frame_rate = 1D / videoPreset.getFramerate();
            }

            // TODO implement pix_fmt, gamut, target color space
            target.registerVideoSubstream(videoPreset.getVideoCodecName(), new VideoFormat(width, height, frame_rate), new HashMap<>());

            // TODO implement processing for audio tracks
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