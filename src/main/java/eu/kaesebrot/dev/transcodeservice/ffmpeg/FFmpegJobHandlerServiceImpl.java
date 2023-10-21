package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import eu.kaesebrot.dev.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.dev.transcodeservice.services.TranscodeJobRepository;
import eu.kaesebrot.dev.transcodeservice.services.TranscodeJobService;
import eu.kaesebrot.dev.transcodeservice.utils.TranscodingUtils;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class FFmpegJobHandlerServiceImpl implements JobHandlerService {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final FFmpegExecutor fFmpegExecutor = new FFmpegExecutor();
    private final Map<Long, FFmpegJob> submittedTasks = new HashMap<>();
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
        FFmpegProbeResult in;
        try {
            in = new FFprobe().probe(transcodeJob.getInFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var builder = TranscodingUtils.generateTranscoder(transcodeJob, in, transcodeJob.getPreset().getTrackPresets());
        FFmpegJob job = fFmpegExecutor.createJob(builder, new ProgressListener() {

            // Using the FFmpegProbeResult determine the duration of the input
            final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);
            final long jobId = transcodeJob.getId();

            @Override
            public void progress(Progress progress) {
                double percentage = progress.out_time_ns / duration_ns;

                // set progress in the progressMap
                progressMap.put(jobId, percentage);

                // Print out interesting information about the progress
                logger.info(String.format(
                        "[%.0f%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx",
                        percentage * 100,
                        progress.status,
                        progress.frame,
                        FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
                        progress.fps.doubleValue(),
                        progress.speed
                ));
            }
        });

        var future = executor.submit(job);

        progressMap.put(transcodeJob.getId(), 0.0D);
        submittedTasks.put(transcodeJob.getId(), job);
        jobService.setJobStatus(transcodeJob.getId(), ETranscodeServiceStatus.QUEUED);
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
        return progressMap.get(jobId);
    }

    @Override
    public double getProgress(TranscodeJob job) throws NoSuchElementException {
        return getProgress(job.getId());
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