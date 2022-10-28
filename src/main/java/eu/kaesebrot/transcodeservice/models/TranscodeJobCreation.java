package eu.kaesebrot.transcodeservice.models;

import java.io.Serializable;
import java.util.UUID;

public class TranscodeJobCreation implements Serializable {
    private String InFile;

    private String OutFolder;
    private UUID PresetId;

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

    public UUID getPresetId() {
        return PresetId;
    }

    public void setPresetId(UUID presetId) {
        PresetId = presetId;
    }

    public TranscodeJobCreation(String inFile, String outFolder, UUID presetId) {
        InFile = inFile;
        OutFolder = outFolder;
        PresetId = presetId;
    }
}
