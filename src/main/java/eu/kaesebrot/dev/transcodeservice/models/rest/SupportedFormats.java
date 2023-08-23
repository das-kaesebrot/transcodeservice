package eu.kaesebrot.dev.transcodeservice.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class SupportedFormats implements Serializable {
    @JsonProperty("supported_video_encoders")
    private List<String> supportedVideoEncoders;
    @JsonProperty("supported_audio_encoders")
    private List<String> supportedAudioEncoders;
    @JsonProperty("supported_containers")
    private List<String> supportedMuxerNames;

    public List<String> getSupportedVideoEncoders() {
        return supportedVideoEncoders;
    }

    public void setSupportedVideoEncoders(List<String> supportedVideoEncoders) {
        this.supportedVideoEncoders = supportedVideoEncoders;
    }

    public List<String> getSupportedAudioEncoders() {
        return supportedAudioEncoders;
    }

    public void setSupportedAudioEncoders(List<String> supportedAudioEncoders) {
        this.supportedAudioEncoders = supportedAudioEncoders;
    }

    public List<String> getSupportedMuxerNames() {
        return supportedMuxerNames;
    }

    public void setSupportedMuxerNames(List<String> supportedMuxerNames) {
        this.supportedMuxerNames = supportedMuxerNames;
    }
}
