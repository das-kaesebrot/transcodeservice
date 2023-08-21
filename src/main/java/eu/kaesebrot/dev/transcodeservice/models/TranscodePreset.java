package eu.kaesebrot.dev.transcodeservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

// TODO
@Entity
@Table(name = "transcode_preset")
public class TranscodePreset implements Serializable {

    @javax.persistence.Version
    @Column(name = "version")
    @JsonProperty("version")
    private long version;

    @javax.persistence.Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    @JsonProperty("id")
    private Long id;

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

    /*
    * TODO
    description = Column(String)

    vcodec = Column(String)
    acodec = Column(String)
    vbitrate = Column(BigInteger)
    abitrate = Column(BigInteger)
    format = Column(String(32))

    width = Column(Integer)
    height = Column(Integer)
    framerate = Column(Float)
    audiorate = Column(BigInteger)

    # for usage with x264/x265
    profile = Column(String)
    tune = Column(String)
    crf = Column(Integer)

    videofilter = Column(String)
    audiofilter = Column(String)

    pix_fmt = Column(String)

    jobs = relationship("TranscodeJob", back_populates="preset")
    */

}
