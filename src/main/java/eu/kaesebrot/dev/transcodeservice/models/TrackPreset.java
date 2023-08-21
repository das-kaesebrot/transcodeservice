package eu.kaesebrot.dev.transcodeservice.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonSubTypes({
        @JsonSubTypes.Type(VideoTrackPreset.class),
        @JsonSubTypes.Type(AudioTrackPreset.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
public abstract class TrackPreset implements Serializable {

}
