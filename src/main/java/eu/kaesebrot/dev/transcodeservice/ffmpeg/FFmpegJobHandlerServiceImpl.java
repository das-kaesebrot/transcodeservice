package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import eu.kaesebrot.dev.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.dev.transcodeservice.services.TranscodeJobRepository;
import eu.kaesebrot.dev.transcodeservice.services.TranscodeJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class FFmpegJobHandlerServiceImpl implements JobHandlerService {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final Map<Long, Future<?>> submittedTasks = new HashMap<>();
    private final TranscodeJobService jobService;
    private final TranscodeJobRepository jobRepository;
    private final Logger logger = LoggerFactory.getLogger(FFmpegJobHandlerServiceImpl.class);
    public FFmpegJobHandlerServiceImpl(TranscodeJobService jobService, TranscodeJobRepository jobRepository) {
        this.jobService = jobService;
        this.jobRepository = jobRepository;

        runHousekeepingJob();
    }

    @Override
    public void submit(Long jobId) {
        submit(jobService.getJob(jobId));
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

    private void runHousekeepingJob() {
        TimerTask task = new TimerTask() {
            private final Logger logger = LoggerFactory.getLogger("jobhandler-housekeeping");
            @Transactional
            public void run() {
                logger.debug("Starting housekeeping run");

                if (submittedTasks.isEmpty())
                    return;

                var repoStatusLists = jobRepository.findStatuses(submittedTasks.keySet());
                Map<Long, ETranscodeServiceStatus> repoStatusMap = HashMap.newHashMap(repoStatusLists.size());

                // Place results in map
                for (Object[] results: repoStatusLists) {
                    repoStatusMap.put((Long) results[0], (ETranscodeServiceStatus) results[1]);
                }

                for (var entry : submittedTasks.entrySet()) {
                    var state = entry.getValue().state();
                    Long jobId = entry.getKey();

                    ETranscodeServiceStatus newStatus = null;

                    switch (state) {
                        case RUNNING -> newStatus = ETranscodeServiceStatus.RUNNING;
                        case CANCELLED -> newStatus = ETranscodeServiceStatus.ABORTED;
                        case FAILED -> newStatus = ETranscodeServiceStatus.FAILED;
                        case SUCCESS -> newStatus = ETranscodeServiceStatus.SUCCESS;
                    }

                    if (newStatus != repoStatusMap.get(jobId)) {
                        logger.info(String.format("Setting status %s for job %s", newStatus.name(), jobId));
                        jobService.setJobStatus(jobId, newStatus);
                    }

                    if (entry.getValue().isDone()) {
                        submittedTasks.remove(jobId);
                    }
                }

                logger.debug("Housekeeping completed");
            }
        };

        Timer timer = new Timer("Timer");
        long delay = 1000L;
        long period = 1000L;
        timer.scheduleAtFixedRate(task, delay, period);
    }
}