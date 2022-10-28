package eu.kaesebrot.transcodeservice.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

// TODO
@Entity
@Table(name = "transcode_preset")
public class TranscodePreset implements Serializable {

    @javax.persistence.Version
    @Column(name = "version")
    private long Version;

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private long Id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Timestamp CreatedAt;

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private Timestamp ModifiedAt;

    public Timestamp getCreatedAt() {
        return CreatedAt;
    }

    public Timestamp getModifiedAt() {
        return ModifiedAt;
    }

    public long getVersion() {
        return Version;
    }

    public long getId() {
        return Id;
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
