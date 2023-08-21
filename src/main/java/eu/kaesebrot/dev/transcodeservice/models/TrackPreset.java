package eu.kaesebrot.dev.transcodeservice.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import java.io.Serializable;

@JsonSubTypes({
        @JsonSubTypes.Type(VideoTrackPreset.class),
        @JsonSubTypes.Type(AudioTrackPreset.class),
})
public abstract class TrackPreset implements Serializable {

}
