package eu.kaesebrot.transcodeservice.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.util.UUID;

public class TranscodeJob {
    @javax.persistence.Version
    @Column(name = "version")
    private long Version;

    @Id
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID Id;

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

    @OneToOne
    @JoinColumn(name = "preset_id")
    private TranscodePreset Preset;

    @OneToOne
    @JoinColumn(name = "status_id")
    private Status Status;

    public long getVersion() {
        return Version;
    }

    public UUID getId() {
        return Id;
    }

    public Timestamp getCreatedAt() {
        return CreatedAt;
    }

    public Timestamp getModifiedAt() {
        return ModifiedAt;
    }

    public eu.kaesebrot.transcodeservice.models.Status getStatus() {
        return Status;
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
                ", Status=" + Status +
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

    public TranscodeJob(
            String inFile,
            String outFolder,
            UUID presetId) {
        InFile = inFile;
        OutFolder = outFolder;

        // retrieve the transcoding preset by ID here
        // TranscodePreset = presetService.getById...;

        Status = new Status();
    }
}
