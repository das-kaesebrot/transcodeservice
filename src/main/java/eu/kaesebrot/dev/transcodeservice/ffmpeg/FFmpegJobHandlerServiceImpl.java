package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import com.github.manevolent.ffmpeg4j.transcoder.Transcoder;
import eu.kaesebrot.dev.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.dev.transcodeservice.services.ITranscodePresetRepository;
import eu.kaesebrot.dev.transcodeservice.services.TranscodeJobService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class FFmpegJobHandlerServiceImpl implements JobHandlerService {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final Map<Long, Future<?>> submittedTasks = new HashMap<>();
    private final Map<Long, Transcoder> transcoders = new HashMap<>();
    private final ITranscodePresetRepository presetRepository;
    private final TranscodeJobService jobService;
    public FFmpegJobHandlerServiceImpl(ITranscodePresetRepository presetRepository, TranscodeJobService jobService) {
        this.presetRepository = presetRepository;
        this.jobService = jobService;
    }

    @Override
    public void submit(Long jobId) {

    }

    @Override
    public void submit(TranscodeJob job) {
        FFmpegTranscodingCallable transcodingCallable = new FFmpegTranscodingCallable(job, job.getPreset(), job.getPreset().getTrackPresets());

        Future<?> transcodingFuture = executor.submit(transcodingCallable);
        submittedTasks.put(job.getId(), transcodingFuture);
        jobService.setJobStatus(job.getId(), ETranscodeServiceStatus.QUEUED);
    }

    @Override
    public List<TranscodeJob> getCompletedTasks() throws NoSuchElementException {
        List<TranscodeJob> resultList = new ArrayList<>();

        for (var taskKeyValuePair : submittedTasks.entrySet()) {
            if (taskKeyValuePair.getValue().isDone())
                resultList.add(jobService.getJob(taskKeyValuePair.getKey()));
        }

        return resultList;
    }

    @Override
    public double getProgress(Long jobId) throws NoSuchElementException {
        return 0;
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
}