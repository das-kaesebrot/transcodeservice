package eu.kaesebrot.dev.transcodeservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.kaesebrot.dev.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.dev.transcodeservice.utils.StringUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;

@Entity
@Table(name = "transcode_job")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TranscodeJob implements Serializable {
    @Version
    @Column(name = "version")
    private long version;

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotBlank(message = "{notEmpty}")
    @Column(name = "in_file", nullable = false)
    @JsonProperty("in_file")
    private String inFile;

    @NotBlank(message = "{notEmpty}")
    @Column(name = "out_folder", nullable = false)
    @JsonProperty("out_folder")
    private String outFolder;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @JsonProperty("created_at")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    @JsonProperty("modified_at")
    private Timestamp modifiedAt;

    @ManyToOne
    @JoinColumn(name = "preset_id")
    @JsonIgnore
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

    @JsonProperty("preset_id")
    public Long getPresetId() {
        return preset.getId();
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

    @JsonIgnore
    public Path getOutFileName() {
        if (StringUtils.isNullOrEmpty(inFile))
            return null;

        if (preset == null)
            return null;

        var file = new File(inFile);

        String fileName = StringUtils.getFilenameWithoutExtension(file.getName());
        String ext = StringUtils.getFileExtension(file.getName());

        return Paths.get(outFolder, fileName + "." + ext);
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
        if (status.ordinal() < this.status.ordinal()) {
            throw new IllegalArgumentException("Status can't be set to a lower value!");
        }

        this.status = status;
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
