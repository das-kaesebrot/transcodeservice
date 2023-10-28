package eu.kaesebrot.dev.transcodeservice.utils;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import eu.kaesebrot.dev.transcodeservice.constants.ETrackPresetType;
import eu.kaesebrot.dev.transcodeservice.models.AudioTrackPreset;
import eu.kaesebrot.dev.transcodeservice.models.TrackPreset;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.dev.transcodeservice.models.VideoTrackPreset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;

public final class TranscodingUtils {
    private static final Logger logger = LoggerFactory.getLogger("TranscodingUtils");
    private TranscodingUtils() {}

    public static FFmpeg generateTranscoder(TranscodeJob job, FFprobeResult inFileProbeResult, Set<TrackPreset> trackPresetSet) {
        var builder = FFmpegFactory.getFFmpeg();
        logger.info("Generating transcoder options");

        File inFile = new File(job.getInFile());

        if (!inFile.isFile())
            throw new IllegalArgumentException("File does not exist at given path! Path: " + job.getInFile());

        logger.debug("Generating new transcoder from preset");

        VideoTrackPreset videoPreset = (VideoTrackPreset) trackPresetSet.stream()
                .filter(t -> t.getType().equals(ETrackPresetType.VIDEO))
                .findFirst().orElseThrow();

        AudioTrackPreset audioPreset = (AudioTrackPreset) trackPresetSet.stream()
                .filter(t -> t.getType().equals(ETrackPresetType.AUDIO))
                .findFirst().orElseThrow();

        Stream inputVideoStream = inFileProbeResult.getStreams().stream()
                .filter(t -> t.getCodecType().equals(StreamType.VIDEO))
                .findFirst().orElseThrow();

        Stream firstInputAudioStream = inFileProbeResult.getStreams().stream()
                .filter(t -> t.getCodecType().equals(StreamType.AUDIO))
                .findFirst().orElseThrow();


        builder
                .addInput(UrlInput.fromUrl(job.getInFile()))
                .setOverwriteOutput(true)
                .addArguments("-c:v", videoPreset.getVideoCodecName());

        int width = inputVideoStream.getWidth();
        int height = inputVideoStream.getHeight();
        double frame_rate = inputVideoStream.getRFrameRate().doubleValue();

        if (videoPreset.getWidth() != null && videoPreset.getWidth() != width) {
            width = videoPreset.getWidth();
        }
        if (videoPreset.getHeight() != null && videoPreset.getHeight() != height) {
            height = videoPreset.getHeight();
        }
        if (videoPreset.getFramerate() != null && (videoPreset.getFramerate()) != frame_rate) {
            frame_rate = videoPreset.getFramerate();
            builder.addArguments("-r", Double.toString(frame_rate));
        }

        // TODO implement gamut, target color space

        if (!StringUtils.isNullOrEmpty(videoPreset.getVideoBitrate()))
            builder.addArguments("-b:v", videoPreset.getVideoBitrate());

        if (!StringUtils.isNullOrEmpty(videoPreset.getVideoPixelFormat()))
            builder.addArguments("-pix_fmt", videoPreset.getVideoPixelFormat());

        logger.debug(String.format("[VIDEO] Properties: %dx%d @%fFPS", width, height, frame_rate));
        logger.debug(String.format("[VIDEO] Codec: %s", videoPreset.getVideoCodecName()));


        // TODO implement support for multiple audio tracks

        int samplerate = firstInputAudioStream.getSampleRate();

        if (audioPreset.getAudioSampleRate() != null && audioPreset.getAudioSampleRate() != samplerate) {
            samplerate = audioPreset.getAudioSampleRate();
            builder.addArguments("-ar", Integer.toString(samplerate));
        }

        logger.debug(String.format("[AUDIO] Properties: %s", samplerate));
        logger.debug(String.format("[AUDIO] Codec: %s", audioPreset.getAudioCodecName()));

        builder
                .addArguments("-f", job.getPreset().getMuxer())
                .addOutput(UrlOutput.toPath(job.getOutFileName()));

        return builder;
    }
}
