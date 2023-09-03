package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import com.github.manevolent.ffmpeg4j.transcoder.Transcoder;

import java.util.concurrent.Callable;

public class FFmpegTranscodingCallable implements Callable<Void> {
    private final Transcoder transcoder;

    public FFmpegTranscodingCallable(Transcoder transcoder) {
        this.transcoder = transcoder;
    }

    @Override
    public Void call() throws Exception {
        transcoder.transcode();
        return null;
    }
}
