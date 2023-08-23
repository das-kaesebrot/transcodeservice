package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import java.util.List;
import java.util.concurrent.Callable;

public interface FFmpegJobHandlerService {
    void submit(Callable<?> task);
    List<? extends Callable<?>> getCompletedTasks();
}
