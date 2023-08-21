package eu.kaesebrot.dev.transcodeservice.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class TranscodeJobCreation implements Serializable {
    @JsonProperty("in_file")
    private String inFile;
    @JsonProperty("out_folder")
    private String outFolder;
    @JsonProperty("preset_id")
    private Long presetId;

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

    public TranscodeJobCreation(String inFile, String outFolder, Long presetId) {
        this.inFile = inFile;
        this.outFolder = outFolder;
        this.presetId = presetId;
    }
}
