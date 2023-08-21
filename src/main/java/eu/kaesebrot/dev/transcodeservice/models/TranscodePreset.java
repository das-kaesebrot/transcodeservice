package eu.kaesebrot.dev.transcodeservice.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "transcode_preset")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TranscodePreset implements Serializable {

    @Version
    @Column(name = "version")
    @JsonProperty("version")
    private long version;

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    @JsonProperty("id")
    private Long id;

    private String description;

    @JsonProperty("container")
    private String muxer;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @JsonProperty("created_at")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    @JsonProperty("modified_at")
    private Timestamp modifiedAt;

    @OneToMany(mappedBy = "preset")
    private Set<TranscodeJob> jobs;

    @ElementCollection
    @JsonProperty("track_presets")
    private Set<TrackPreset> trackPresets = new HashSet<>();

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getModifiedAt() {
        return modifiedAt;
    }

    public long getVersion() {
        return version;
    }

    public Long getId() {
        return id;
    }

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

    public Set<TranscodeJob> getJobs() {
        return jobs;
    }

    public void setJobs(Set<TranscodeJob> jobs) {
        this.jobs = jobs;
    }

    public Set<TrackPreset> getTrackPresets() {
        return trackPresets;
    }

    public void setTrackPresets(Set<TrackPreset> trackPresets) {
        this.trackPresets = trackPresets;
    }
/*
    * TODO
    # for usage with x264/x265
    profile = Column(String)
    tune = Column(String)
    crf = Column(Integer)

    videofilter = Column(String)
    audiofilter = Column(String)
    */

}
