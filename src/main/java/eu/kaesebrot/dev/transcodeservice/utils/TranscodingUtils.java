package eu.kaesebrot.dev.transcodeservice.utils;

import eu.kaesebrot.dev.transcodeservice.constants.ETrackPresetType;
import eu.kaesebrot.dev.transcodeservice.models.AudioTrackPreset;
import eu.kaesebrot.dev.transcodeservice.models.TrackPreset;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.dev.transcodeservice.models.VideoTrackPreset;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;

public final class TranscodingUtils {
    private static final Logger logger = LoggerFactory.getLogger("TranscodingUtils");
    private TranscodingUtils() {}

    public static FFmpegBuilder generateTranscoder(TranscodeJob job, FFmpegProbeResult inFileProbeResult, Set<TrackPreset> trackPresetSet) {
        logger.info("Generating transcoder options");

        File inFile = new File(job.getInFile());

        if (!inFile.isFile())
            throw new IllegalArgumentException("File does not exist at given path! Path: " + job.getInFile());

        logger.debug("Generating new transcoder from preset");

        FFmpegOutputBuilder builder = new FFmpegBuilder()
                .setInput(inFileProbeResult)
                .addOutput(job.getOutFileName().toString());

        VideoTrackPreset videoPreset = (VideoTrackPreset) trackPresetSet.stream()
                .filter(t -> t.getType().equals(ETrackPresetType.VIDEO))
                .findFirst().orElseThrow();

        AudioTrackPreset audioPreset = (AudioTrackPreset) trackPresetSet.stream()
                .filter(t -> t.getType().equals(ETrackPresetType.AUDIO))
                .findFirst().orElseThrow();

        FFmpegStream inputVideoStream = inFileProbeResult.getStreams().stream()
                .filter(t -> t.codec_type.equals(FFmpegStream.CodecType.VIDEO))
                .findFirst().orElseThrow();

        FFmpegStream firstInputAudioStream = inFileProbeResult.getStreams().stream()
                .filter(t -> t.codec_type.equals(FFmpegStream.CodecType.VIDEO))
                .findFirst().orElseThrow();

        int width = inputVideoStream.width;
        int height = inputVideoStream.height;
        double frame_rate = inputVideoStream.r_frame_rate.doubleValue();

        if (videoPreset.getWidth() != null && videoPreset.getWidth() != width) {
            width = videoPreset.getWidth();
        }
        if (videoPreset.getHeight() != null && videoPreset.getHeight() != height) {
            height = videoPreset.getHeight();
        }
        if (videoPreset.getFramerate() != null && (videoPreset.getFramerate()) != frame_rate) {
            frame_rate = videoPreset.getFramerate();
        }

        builder
                .setVideoWidth(width)
                .setVideoHeight(height)
                .setVideoFrameRate(frame_rate);

        // TODO implement gamut, target color space

        if (!StringUtils.isNullOrEmpty(videoPreset.getVideoBitrate()))
            builder.setVideoBitRate(FFmpegUtils.parseBitrate(videoPreset.getVideoBitrate()));

        if (!StringUtils.isNullOrEmpty(videoPreset.getVideoPixelFormat()))
            builder.setVideoPixelFormat(videoPreset.getVideoPixelFormat());

        logger.debug(String.format("[VIDEO] Properties: %dx%d @%fFPS", width, height, frame_rate));
        logger.debug(String.format("[VIDEO] Codec: %s", videoPreset.getVideoCodecName()));


        // TODO implement support for multiple audio tracks

        int samplerate = firstInputAudioStream.sample_rate;

        if (audioPreset.getAudioSampleRate() != null && audioPreset.getAudioSampleRate() != samplerate) {
            samplerate = audioPreset.getAudioSampleRate();
        }
        builder
                .setAudioSampleRate(samplerate);


        logger.debug(String.format("[AUDIO] Properties: %s", samplerate));
        logger.debug(String.format("[AUDIO] Codec: %s", audioPreset.getAudioCodecName()));

        return builder.done();
    }
}
