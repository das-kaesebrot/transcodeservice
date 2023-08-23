package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class FFmpegJobHandlerServiceImpl implements FFmpegJobHandlerService {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final Map<Callable<?>, Future> submittedTasks = new HashMap<>();
    public FFmpegJobHandlerServiceImpl() {

    }

    @Override
    public void submit(Callable<?> task) {
        var future = executor.submit(task);
        submittedTasks.put(task, future);
    }

    @Override
    public List<? extends Callable<?>> getCompletedTasks() {
        return submittedTasks.entrySet().stream()
                .filter(e -> e.getValue().isDone())
                .map(Map.Entry::getKey)
                .toList();
    }
}