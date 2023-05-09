package eu.kaesebrot.transcodeservice.ffmpeg;

import java.util.List;

public class FfmpegHandler {
    private List<FfmpegJob> JobQueue;
    private boolean RunLoop;

    public synchronized void runDispatcherLoop() {
        RunLoop = true;

        do {
            for (var job: JobQueue) {
                startJob(job);
                JobQueue.remove(job);
            }
        } while(RunLoop);
    }

    private boolean addJobToQueue(FfmpegJob ffmpegJob) {
        return JobQueue.add(ffmpegJob);
    }

    public void stopLoopGracefully() {
        RunLoop = false;
    }

    private void startJob(FfmpegJob ffmpegJob) {

    }
}
