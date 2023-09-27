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
import eu.kaesebrot.dev.transcodeservice.models.AudioTrackPreset;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import eu.kaesebrot.dev.transcodeservice.models.VideoTrackPreset;
import eu.kaesebrot.dev.transcodeservice.services.ITranscodeJobRepository;
import eu.kaesebrot.dev.transcodeservice.utils.AVUtils;
import eu.kaesebrot.dev.transcodeservice.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

@Service
public class FFmpegJobHandlerServiceImpl implements FFmpegJobHandlerService {
    private final ExecutorService executor = Executors.newWorkStealingPool();
    private final Map<Long, Future<?>> submittedTasks = new HashMap<>();
    private final Map<Long, Transcoder> transcoders = new HashMap<>();
    private final ITranscodeJobRepository transcodeJobRepository;
    public FFmpegJobHandlerServiceImpl(ITranscodeJobRepository transcodeJobRepository) {
        this.transcodeJobRepository = transcodeJobRepository;
    }

    @Override
    public void submit(TranscodeJob job) {
        Transcoder transcoder = getTranscoderForJob(job);
        FFmpegTranscodingCallable transcodingCallable = new FFmpegTranscodingCallable(transcoder);

        Future<?> transcodingFuture = executor.submit(transcodingCallable);
        submittedTasks.put(job.getId(), transcodingFuture);
    }

    @Override
    public List<TranscodeJob> getCompletedTasks() throws NoSuchElementException {
        List<TranscodeJob> resultList = new ArrayList<>();

        for (var taskKeyValuePair : submittedTasks.entrySet()) {
            if (taskKeyValuePair.getValue().isDone())
                resultList.add(transcodeJobRepository.findById(taskKeyValuePair.getKey()).orElseThrow());
        }

        return resultList;
    }

    @Override
    public double getProgress(TranscodeJob job) throws NoSuchElementException {
        /*
        return transcoders.entrySet().stream()
                .filter(f -> f.getKey().equals(job.getId()))
                .map(Map.Entry::getValue)
                .findFirst().orElseThrow()
                .getProgress();
         */
        return -1D; // TODO implement progess getter
    }


    /**
     * Either retrieves an existing {@link com.github.manevolent.ffmpeg4j.transcoder.Transcoder Transcoder} object for the specified job
     * or generates one on the fly, adds it to the list of known jobs and returns it.
     * @param job The job to get or generate a {@link com.github.manevolent.ffmpeg4j.transcoder.Transcoder Transcoder} for
     * @return A new or already present {@link com.github.manevolent.ffmpeg4j.transcoder.Transcoder Transcoder}.
     */
    private Transcoder getTranscoderForJob(TranscodeJob job) {
        return getTranscoderForJob(job, false);
    }

    private Transcoder getTranscoderForJob(TranscodeJob job, boolean forceRecreation) {
        if (transcoders.containsKey(job.getId()) && !forceRecreation)
            return transcoders.get(job.getId());

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

            transcoders.put(job.getId(), transcoder);
            return transcoder;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}