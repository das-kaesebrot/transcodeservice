package eu.kaesebrot.dev.transcodeservice.models;

import eu.kaesebrot.dev.transcodeservice.constants.ETranscodeServiceStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "transcode_job")
public class TranscodeJob implements Serializable {
    @javax.persistence.Version
    @Column(name = "version")
    private long version;

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotBlank(message = "{notEmpty}")
    @Column(name = "in_file", nullable = false)
    private String inFile;

    @NotBlank(message = "{notEmpty}")
    @Column(name = "out_folder", nullable = false)
    private String outFolder;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private Timestamp modifiedAt;

    @ManyToOne
    @JoinColumn(name = "preset_id")
    private TranscodePreset preset;

    private ETranscodeServiceStatus status;

    public long getVersion() {
        return version;
    }

    public Long getId() {
        return id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getModifiedAt() {
        return modifiedAt;
    }

    public ETranscodeServiceStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "TranscodeJob{" +
                "Version=" + version +
                ", Id=" + id +
                ", InFile='" + inFile + '\'' +
                ", OutFolder='" + outFolder + '\'' +
                ", CreatedAt=" + createdAt +
                ", ModifiedAt=" + modifiedAt +
                ", Preset=" + preset +
                ", Status=" + status +
                '}';
    }

    public String getInFile() {
        return inFile;
    }

    public void setInFile(String inFile) {
        this.inFile = inFile;
    }

    public String getOutFolder() {
        return outFolder;
    }

    public void setOutFolder(String outFolder) {
        this.outFolder = outFolder;
    }

    public TranscodePreset getPreset() {
        return preset;
    }

    public void setPreset(TranscodePreset preset) {
        this.preset = preset;
    }

    public void setStatus(ETranscodeServiceStatus status) {
        if (status.ordinal() < status.ordinal()) {
            throw new IllegalArgumentException("Status can't be set to a lower value!");
        }

        status = status;
    }

    public TranscodeJob(
            String inFile,
            String outFolder,
            TranscodePreset preset) {
        this.inFile = inFile;
        this.outFolder = outFolder;
        this.preset = preset;

        status = ETranscodeServiceStatus.CREATED;
    }

    public TranscodeJob() {

    }
}
