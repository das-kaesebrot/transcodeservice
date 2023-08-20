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
    private long Version;

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private Long Id;

    @NotBlank(message = "{notEmpty}")
    @Column(name = "in_file", nullable = false)
    private String InFile;

    @NotBlank(message = "{notEmpty}")
    @Column(name = "out_folder", nullable = false)
    private String OutFolder;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Timestamp CreatedAt;

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private Timestamp ModifiedAt;

    @ManyToOne
    @JoinColumn(name = "preset_id")
    private TranscodePreset Preset;

    private ETranscodeServiceStatus TranscodeStatus;

    public long getVersion() {
        return Version;
    }

    public Long getId() {
        return Id;
    }

    public Timestamp getCreatedAt() {
        return CreatedAt;
    }

    public Timestamp getModifiedAt() {
        return ModifiedAt;
    }

    public ETranscodeServiceStatus getTranscodeStatus() {
        return TranscodeStatus;
    }

    @Override
    public String toString() {
        return "TranscodeJob{" +
                "Version=" + Version +
                ", Id=" + Id +
                ", InFile='" + InFile + '\'' +
                ", OutFolder='" + OutFolder + '\'' +
                ", CreatedAt=" + CreatedAt +
                ", ModifiedAt=" + ModifiedAt +
                ", Preset=" + Preset +
                ", Status=" + TranscodeStatus +
                '}';
    }

    public String getInFile() {
        return InFile;
    }

    public void setInFile(String inFile) {
        InFile = inFile;
    }

    public String getOutFolder() {
        return OutFolder;
    }

    public void setOutFolder(String outFolder) {
        OutFolder = outFolder;
    }

    public TranscodePreset getPreset() {
        return Preset;
    }

    public void setPreset(TranscodePreset preset) {
        Preset = preset;
    }

    public void setTranscodeStatus(ETranscodeServiceStatus status) {
        if (status.ordinal() < TranscodeStatus.ordinal()) {
            throw new IllegalArgumentException("Status can't be set to a lower value!");
        }

        TranscodeStatus = status;
    }

    public TranscodeJob(
            String inFile,
            String outFolder,
            TranscodePreset preset) {
        InFile = inFile;
        OutFolder = outFolder;
        Preset = preset;

        TranscodeStatus = ETranscodeServiceStatus.CREATED;
    }

    public TranscodeJob() {

    }
}
