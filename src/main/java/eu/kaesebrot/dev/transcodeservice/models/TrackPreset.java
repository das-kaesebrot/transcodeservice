package eu.kaesebrot.dev.transcodeservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.kaesebrot.dev.transcodeservice.constants.ETrackPresetType;

import java.io.Serializable;

@JsonSubTypes({
        @JsonSubTypes.Type(VideoTrackPreset.class),
        @JsonSubTypes.Type(AudioTrackPreset.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
public abstract class TrackPreset implements Serializable {
    @JsonIgnore
    public abstract ETrackPresetType getType();
}
