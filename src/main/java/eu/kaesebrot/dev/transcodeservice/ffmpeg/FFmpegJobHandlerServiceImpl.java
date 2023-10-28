package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import com.github.kokorin.jaffree.ffmpeg.*;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import eu.kaesebrot.dev.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.dev.transcodeservice.services.TranscodeJobRepository;
import eu.kaesebrot.dev.transcodeservice.services.TranscodeJobService;
import eu.kaesebrot.dev.transcodeservice.utils.FFmpegFactory;
import eu.kaesebrot.dev.transcodeservice.utils.TranscodingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class FFmpegJobHandlerServiceImpl implements JobHandlerService {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final Map<Long, FFmpegResultFuture> runningTasks = new HashMap<>();
    private final Map<Long, Double> progressMap = new HashMap<>();
    private final TranscodeJobService jobService;
    private final TranscodeJobRepository jobRepository;
    private final Logger logger = LoggerFactory.getLogger(FFmpegJobHandlerServiceImpl.class);
    public FFmpegJobHandlerServiceImpl(TranscodeJobService jobService, TranscodeJobRepository jobRepository) throws IOException {
        this.jobService = jobService;
        this.jobRepository = jobRepository;

        runHousekeepingJob();
    }

    @Override
    public void submit(Long jobId) {
        submit(jobService.getJob(jobId));
    }

    @Override
    public void submit(TranscodeJob transcodeJob) {
        FFprobeResult in = FFmpegFactory.getFFprobe()
                .setInput(transcodeJob.getInFile())
                .setShowStreams(true)
                .setShowFormat(true)
                .execute();

        FFmpeg job = TranscodingUtils.generateTranscoder(transcodeJob, in, transcodeJob.getPreset().getTrackPresets())
                .setProgressListener(new ProgressListener() {

                    // Using the FFmpegProbeResult determine the duration of the input
                    final double duration_ns = in.getFormat().getDuration() * TimeUnit.SECONDS.toNanos(1);
                    final long jobId = transcodeJob.getId();

                    @Override
                    public void onProgress(FFmpegProgress progress) {
                        double percentage = progress.getTime(TimeUnit.NANOSECONDS) / duration_ns;

                        // set progress in the progressMap
                        progressMap.put(jobId, percentage);

                        // Print out interesting information about the progress
                        logger.debug(String.format(
                                "[%.0f%%] frame:%d fps:%.0f speed:%.2fx",
                                percentage * 100,
                                progress.getFrame(),
                                progress.getFps(),
                                progress.getSpeed()
                        ));
                    }
                })
        ;
        
        executor.submit(new Callable<FFmpegResultFuture>() {
            @Override
            public FFmpegResultFuture call() throws Exception {
                FFmpegResultFuture future = job.executeAsync();
                runningTasks.put(transcodeJob.getId(), future);
                return future;
            }
        });

        progressMap.put(transcodeJob.getId(), 0.0D);
        jobService.setJobStatus(transcodeJob.getId(), ETranscodeServiceStatus.QUEUED);
    }

    @Override
    public List<TranscodeJob> getCompletedTasks() throws NoSuchElementException {
        List<TranscodeJob> resultList = new ArrayList<>();

        for (var taskKeyValuePair : submittedTasks.entrySet()) {
            if (jobDone(taskKeyValuePair.getValue()))
                resultList.add(jobService.getJob(taskKeyValuePair.getKey()));
        }

        return resultList;
    }

    @Override
    public double getProgress(Long jobId) throws NoSuchElementException {
        return progressMap.get(jobId);
    }

    @Override
    public double getProgress(TranscodeJob job) throws NoSuchElementException {
        return getProgress(job.getId());
    }

    private void runHousekeepingJob() {
        TimerTask task = new TimerTask() {
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
                    var state = entry.getValue().getState();
                    Long jobId = entry.getKey();

                    ETranscodeServiceStatus newStatus = null;

                    switch (state) {
                        case RUNNING -> newStatus = ETranscodeServiceStatus.RUNNING;
                        case WAITING -> newStatus = ETranscodeServiceStatus.QUEUED;
                        case FAILED -> newStatus = ETranscodeServiceStatus.FAILED;
                        case FINISHED -> newStatus = ETranscodeServiceStatus.SUCCESS;
                    }

                    if (newStatus != repoStatusMap.get(jobId)) {
                        logger.info(String.format("Setting status %s for job %s", newStatus.name(), jobId));
                        jobService.setJobStatus(jobId, newStatus);
                    }

                    if (jobDone(entry.getValue())) {
                        progressMap.remove(jobId);
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

    private boolean jobDone(FFmpegJob job) {
        return job.getState() == FFmpegJob.State.FINISHED
                || job.getState() == FFmpegJob.State.FAILED;
    }
}