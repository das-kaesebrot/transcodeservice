package eu.kaesebrot.dev.transcodeservice.models.rest;

import java.io.Serializable;

public class TranscodeJobCreation implements Serializable {
    private String inFile;
    private String outFolder;
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
