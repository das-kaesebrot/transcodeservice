package eu.kaesebrot.dev.transcodeservice.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.kaesebrot.dev.transcodeservice.models.TrackPreset;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class TranscodePresetUpdate implements Serializable {
    @JsonProperty("description")
    private String description;

    @JsonProperty("muxer")
    private String muxer;

    @ElementCollection
    @Column(length=99999)
    @JsonProperty("track_presets")
    private Set<TrackPreset> trackPresets = new HashSet<>();

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

    public TranscodePresetUpdate(String description, String muxer, Set<TrackPreset> trackPresets) {
        this.description = description;
        this.muxer = muxer;
        this.trackPresets = trackPresets;
    }
}
