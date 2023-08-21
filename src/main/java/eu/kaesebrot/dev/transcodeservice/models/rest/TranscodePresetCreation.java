package eu.kaesebrot.dev.transcodeservice.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.kaesebrot.dev.transcodeservice.models.TrackPreset;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

public class TranscodePresetCreation implements Serializable {
    private String description;
    @NotNull
    @JsonProperty("container")
    private String muxer;

    @NotEmpty
    @JsonProperty("track_presets")
    private Set<TrackPreset> trackPresets;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getMuxer() {
        return muxer;
    }

    public void setMuxer(String muxer) {
        this.muxer = muxer;
    }

    public Set<TrackPreset> getTrackPresets() {
        return trackPresets;
    }

    public void setTrackPresets(Set<TrackPreset> trackPresets) {
        this.trackPresets = trackPresets;
    }
}
