package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FFmpegJobExecutor implements Executor {

    private final Executor executor;
    private final Queue<Runnable> tasks = new ArrayDeque<>();
    private Runnable active = null;

    public FFmpegJobExecutor() {
        this.executor = Executors.newFixedThreadPool(10);
    }

    @Override
    public synchronized void execute(Runnable job) {
        tasks.add(() -> {
            try {
                job.run();
            } finally {
                scheduleNext();
            }
        });

        if (active == null) {
            scheduleNext();
        }
    }

    private synchronized void scheduleNext() {
        if ((active = tasks.poll()) != null) {
            executor.execute(active);
        }
    }
}