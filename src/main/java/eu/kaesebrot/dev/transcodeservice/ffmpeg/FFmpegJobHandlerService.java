package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import java.util.concurrent.Callable;
import java.util.stream.Stream;

public interface FFmpegJobHandlerService {
    void submit(Callable<?> task);
    Stream<Callable<?>> getCompletedTasks();
}
