package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Stream;

@Service
public class FFmpegJobHandlerServiceImpl implements FFmpegJobHandlerService {

    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final Map<Callable<?>, Future> submittedTasks = new HashMap<>();
    private Runnable active = null;

    public FFmpegJobHandlerServiceImpl() {

    }

    @Override
    public void submit(Callable<?> task) {
        submittedTasks.put(task, executor.submit(task));
    }

    @Override
    public Stream<Callable<?>> getCompletedTasks() {
        return submittedTasks.entrySet().stream()
                .filter(e -> e.getValue().isDone())
                .map(e -> e.getKey());
    }
}