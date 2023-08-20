package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import java.io.IOException;
import java.util.List;

public interface FfmpegUtilities {
    List<String> getSupportedVideoCodecs() throws IOException;
    List<String> getSupportedAudioCodecs();
}
