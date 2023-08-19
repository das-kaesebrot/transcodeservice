package eu.kaesebrot.dev.transcodeservice.models.rest;

import java.io.Serializable;

public class TranscodeJobCreation implements Serializable {
    private String InFile;
    private String OutFolder;
    private Long PresetId;

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

    public Long getPresetId() {
        return PresetId;
    }

    public void setPresetId(Long presetId) {
        PresetId = presetId;
    }

    public TranscodeJobCreation(String inFile, String outFolder, Long presetId) {
        InFile = inFile;
        OutFolder = outFolder;
        PresetId = presetId;
    }
}
