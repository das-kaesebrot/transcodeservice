package eu.kaesebrot.dev.transcodeservice.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.kaesebrot.dev.transcodeservice.utils.FFmpegFactory;

import java.io.Serializable;

public class FFmpegPaths implements Serializable {
    @JsonProperty("ffmpeg")
    public String getFFmpegPath() {
        return FFmpegFactory.getFFmpegPath().toAbsolutePath().toString();
    }
    @JsonProperty("ffprobe")
    public String getFFprobePath() {
        return FFmpegFactory.getFFprobePath().toAbsolutePath().toString();
    }
}
