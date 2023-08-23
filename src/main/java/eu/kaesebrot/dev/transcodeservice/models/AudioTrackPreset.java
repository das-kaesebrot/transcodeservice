package eu.kaesebrot.dev.transcodeservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.kaesebrot.dev.transcodeservice.constants.ETrackPresetType;
import eu.kaesebrot.dev.transcodeservice.utils.AVUtils;
import eu.kaesebrot.dev.transcodeservice.utils.StringUtils;
import jakarta.validation.constraints.Positive;
import org.springframework.lang.Nullable;

import java.util.Map;

public class AudioTrackPreset extends TrackPreset {
    @JsonProperty("audio_codec")
    private String audioCodecName;
    @Nullable
    @JsonProperty("audio_bitrate")
    private String audioBitrate;
    @Nullable
    @JsonProperty("audio_sample_rate")
    @Positive
    private Integer audioSampleRate;
    @JsonProperty("audio_options")
    private Map<String, String> audioOptions;

    public String getAudioCodecName() {
        return audioCodecName;
    }

    public void setAudioCodecName(String audioCodecName) {
        if (StringUtils.isNullOrEmpty(audioCodecName)) {
            this.audioCodecName = "copy"; // TODO check this
            return;
        }

        if (!AVUtils.getSupportedAudioEncoders().contains(audioCodecName)) {
            throw new IllegalArgumentException(String.format("Given codec '%s' is not supported!", audioCodecName));
        }

        this.audioCodecName = audioCodecName;
    }

    @Nullable
    public String getAudioBitrate() {
        return audioBitrate;
    }

    public void setAudioBitrate(@Nullable String audioBitrate) {
        this.audioBitrate = audioBitrate;
    }

    @Nullable
    public Integer getAudioSampleRate() {
        return audioSampleRate;
    }

    public void setAudioSampleRate(@Nullable Integer audioSampleRate) {
        this.audioSampleRate = audioSampleRate;
    }

    public Map<String, String> getAudioOptions() {
        return audioOptions;
    }

    public void setAudioOptions(Map<String, String> audioOptions) {
        this.audioOptions = audioOptions;
    }

    @Override
    public ETrackPresetType getType() {
        return ETrackPresetType.AUDIO;
    }
}
