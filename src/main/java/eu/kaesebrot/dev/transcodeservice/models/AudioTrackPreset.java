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
    private Long audioSampleRate;

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
    public Long getAudioSampleRate() {
        return audioSampleRate;
    }

    public void setAudioSampleRate(@Nullable Long audioSampleRate) {
        this.audioSampleRate = audioSampleRate;
    }

    @Override
    public ETrackPresetType getType() {
        return ETrackPresetType.AUDIO;
    }
}
