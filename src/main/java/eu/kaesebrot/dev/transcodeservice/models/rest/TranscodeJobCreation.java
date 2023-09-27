package eu.kaesebrot.dev.transcodeservice.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class TranscodeJobCreation implements Serializable {
    @NotNull
    @JsonProperty("in_file")
    private String inFile;
    @NotNull
    @JsonProperty("out_folder")
    private String outFolder;
    @NotNull
    @JsonProperty("preset_id")
    private Long presetId;

    @JsonProperty("enqueue_immediately")
    private boolean enqueueImmediately = false;

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

    public Long getPresetId() {
        return presetId;
    }

    public void setPresetId(Long presetId) {
        this.presetId = presetId;
    }
    public boolean enqueueImmeditely() {
        return enqueueImmediately;
    }

    public void setEnqueueImmediately(boolean enqueueImmediately) {
        this.enqueueImmediately = enqueueImmediately;
    }

    public TranscodeJobCreation(String inFile, String outFolder, Long presetId) {
        this.inFile = inFile;
        this.outFolder = outFolder;
        this.presetId = presetId;
    }

    public TranscodeJobCreation(String inFile, String outFolder, Long presetId, boolean enqueueImmediately) {
        this(inFile, outFolder, presetId);
        this.enqueueImmediately = enqueueImmediately;
    }
}
