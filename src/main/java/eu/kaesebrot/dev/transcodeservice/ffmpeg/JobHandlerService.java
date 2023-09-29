package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;

import java.util.List;
import java.util.NoSuchElementException;

public interface JobHandlerService {
    void submit(Long jobId);
    void submit(TranscodeJob job);
    List<TranscodeJob> getCompletedTasks() throws NoSuchElementException;
    double getProgress(Long jobId) throws NoSuchElementException;
    double getProgress(TranscodeJob job) throws NoSuchElementException;
}
