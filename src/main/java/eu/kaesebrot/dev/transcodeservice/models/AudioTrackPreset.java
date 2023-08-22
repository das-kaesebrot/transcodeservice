package eu.kaesebrot.dev.transcodeservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.kaesebrot.dev.transcodeservice.constants.ETrackPresetType;
import org.springframework.lang.Nullable;

public class AudioTrackPreset extends TrackPreset {
    @JsonProperty("audio_codec")
    private String audioCodecName;
    @Nullable
    @JsonProperty("audio_bitrate")
    private String audioBitrate;
    @Nullable
    @JsonProperty("audio_sample_rate")
    private Integer audioSampleRate;

    public String getAudioCodecName() {
        return audioCodecName;
    }

    public void setAudioCodecName(String audioCodecName) {
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

    @Override
    public ETrackPresetType getType() {
        return ETrackPresetType.AUDIO;
    }
}
