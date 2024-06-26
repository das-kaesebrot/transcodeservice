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
            final FFmpeg transcoder = job;
            final long jobId = transcodeJob.getId();
            @Override
            public FFmpegResultFuture call() throws Exception {
                logger.debug(String.format("Job %d started", jobId));
                FFmpegResultFuture future = transcoder.executeAsync();
                runningTasks.put(transcodeJob.getId(), future);
                return future;
            }
        });

        progressMap.put(transcodeJob.getId(), 0.0D);
        jobService.setJobStatus(transcodeJob.getId(), ETranscodeServiceStatus.QUEUED);
    }

    @Override
    public void abort(Long jobId) {
        FFmpegResultFuture future = runningTasks.getOrDefault(jobId, null);
        if (future != null) {
            runningTasks.get(jobId).graceStop();
            logger.debug(String.format("Successfully stopped job %d", jobId));
        }
        logger.warn(String.format("No job to stop: %d", jobId));
    }

    @Override
    public List<TranscodeJob> getCompletedTasks() throws NoSuchElementException {
        List<TranscodeJob> resultList = new ArrayList<>();

        for (var taskKeyValuePair : runningTasks.entrySet()) {
            if (jobDone(taskKeyValuePair.getValue()))
                resultList.add(jobService.getJob(taskKeyValuePair.getKey()));
        }

        return resultList;
    }

    @Override
    public double getProgress(Long jobId) throws NoSuchElementException {
        return progressMap.getOrDefault(jobId, -1D);
    }

    @Override
    public double getProgress(TranscodeJob job) throws NoSuchElementException {
        if (job.getStatus() == ETranscodeServiceStatus.SUCCESS) return 1D;

        return getProgress(job.getId());
    }

    private void runHousekeepingJob() {
        TimerTask task = new TimerTask() {
            @Transactional
            public void run() {
                if (runningTasks.isEmpty())
                    return;

                logger.debug("Starting housekeeping run");

                for (var entry : runningTasks.entrySet()) {
                    var state = entry.getValue();
                    Long jobId = entry.getKey();

                    ETranscodeServiceStatus newStatus = null;
                    if (state.isCancelled())
                        newStatus = ETranscodeServiceStatus.FAILED;
                    else if (state.isDone())
                        newStatus = ETranscodeServiceStatus.SUCCESS;
                    else
                        newStatus = ETranscodeServiceStatus.RUNNING;

                    updateJobStatus(jobId, newStatus);

                    if (jobDone(entry.getValue())) {
                        progressMap.remove(jobId);
                        runningTasks.remove(jobId);
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

    private boolean jobDone(FFmpegResultFuture job) {
        return job.isDone() || job.isCancelled();
    }

    private void updateJobStatus(long jobId, ETranscodeServiceStatus status) {
        if (status != jobRepository.getStatus(jobId)) {
            logger.info(String.format("Setting status %s for job %s", status.name(), jobId));
            jobService.setJobStatus(jobId, status);
        }
    }
}