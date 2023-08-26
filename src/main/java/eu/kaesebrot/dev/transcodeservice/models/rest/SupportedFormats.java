package eu.kaesebrot.dev.transcodeservice.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.kaesebrot.dev.transcodeservice.utils.AVUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SupportedFormats implements Serializable {
    @JsonProperty("supported_video_encoders")
    public List<String> getSupportedVideoEncoders() {
        return AVUtils.getSupportedVideoEncoders();
    }
    @JsonProperty("supported_audio_encoders")
    public List<String> getSupportedAudioEncoders() {
        return AVUtils.getSupportedAudioDecoders();
    }
    @JsonProperty("supported_containers")
    public List<String> getSupportedMuxerNames() {
        return AVUtils.getSupportedMuxerNames();
    }
    @JsonProperty("supported_pixel_formats_for_codec")
    public Map<String, List<String>> getSupportedPixelFormatsMap() {
        return AVUtils.getSupportedPixelFormatNames();
    }
    @JsonProperty("supported_audio_samplerates_for_codec")
    public Map<String, List<Integer>> getSupportedSampleratesMap() {
        return AVUtils.getSupportedAudioSampleRates();
    }
}
