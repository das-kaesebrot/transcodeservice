package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import com.github.manevolent.ffmpeg4j.FFmpeg;
import com.github.manevolent.ffmpeg4j.FFmpegIO;
import com.github.manevolent.ffmpeg4j.MediaType;
import com.github.manevolent.ffmpeg4j.VideoFormat;
import com.github.manevolent.ffmpeg4j.filter.audio.AudioFilterNone;
import com.github.manevolent.ffmpeg4j.filter.video.VideoFilterNone;
import com.github.manevolent.ffmpeg4j.source.AudioSourceSubstream;
import com.github.manevolent.ffmpeg4j.source.VideoSourceSubstream;
import com.github.manevolent.ffmpeg4j.stream.output.FFmpegTargetStream;
import com.github.manevolent.ffmpeg4j.stream.source.FFmpegSourceStream;
import com.github.manevolent.ffmpeg4j.transcoder.Transcoder;
import eu.kaesebrot.dev.transcodeservice.constants.ETrackPresetType;
import eu.kaesebrot.dev.transcodeservice.models.*;
import eu.kaesebrot.dev.transcodeservice.utils.AVUtils;
import eu.kaesebrot.dev.transcodeservice.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Callable;

@Transactional
public class FFmpegTranscodingCallable implements Callable<Void> {
    private final TranscodeJob job;
    private final TranscodePreset preset;
    private final Set<TrackPreset> trackPresetSet;
    private final Logger logger;

    public FFmpegTranscodingCallable(TranscodeJob job, TranscodePreset preset, Set<TrackPreset> trackPresetSet) {
        this.job = job;
        this.preset = preset;
        this.trackPresetSet = trackPresetSet;

        this.trackPresetSet.size();
        this.logger = LoggerFactory.getLogger("transcoder-job-" + job.getId());
    }

    @Override
    public Void call() throws Exception {
        var start = Instant.now();
        logger.info("Generating transcoder options");

        File inFile = new File(job.getInFile());
        Path outFilePath = job.getOutFileName();

        if (!inFile.isFile())
            throw new IllegalArgumentException("File does not exist at given path! Path: " + job.getInFile());

        SeekableByteChannel outChannel = Files.newByteChannel(outFilePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        FFmpegTargetStream target = FFmpegIO.openChannel(outChannel).asOutput().open(preset.getMuxer());

        try (FFmpegSourceStream source = FFmpegIO.openInput(inFile).open(AVUtils.getInputFormat(inFile.getAbsolutePath()))) {
            source.registerStreams();

            var videoStream = (VideoSourceSubstream) source.getSubstreams().stream().filter(s -> s.getMediaType() == MediaType.VIDEO).findFirst().orElseThrow();

            VideoTrackPreset videoPreset = (VideoTrackPreset) trackPresetSet.stream()
                    .filter(t -> t.getType().equals(ETrackPresetType.VIDEO))
                    .findFirst().orElseThrow();

            AudioTrackPreset audioPreset = (AudioTrackPreset) trackPresetSet.stream()
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
            if (videoPreset.getFramerate() != null && (videoPreset.getFramerate()) != frame_rate) {
                frame_rate = videoPreset.getFramerate();
            }

            // TODO implement gamut, target color space
            var videoOpts = new HashMap<String, String>();

            if (StringUtils.isNullOrEmpty(videoPreset.getVideoBitrate()))
                videoOpts.put("b", videoPreset.getVideoBitrate());

            target.registerVideoSubstream(videoPreset.getVideoCodecName(), width, height, frame_rate, videoOpts);

            if (!StringUtils.isNullOrEmpty(videoPreset.getVideoPixelFormat()))
                target.setPixelFormat(FFmpeg.getPixelFormatByName(videoPreset.getVideoPixelFormat()));

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

            Transcoder transcoder = new Transcoder(source, target, new AudioFilterNone(), new VideoFilterNone(), Double.MAX_VALUE);

            logger.info("Starting transcoder");

            transcoder.transcode();
            var end = Instant.now();
            logger.info("Transcoding completed successfully");

            logger.info(String.format("Transcoding took %s", Duration.between(start, end)));
        } catch (Exception e) {
            logger.error("Transcoder ran into an exception", e);
            throw e;
        }
        return null;
    }
}
