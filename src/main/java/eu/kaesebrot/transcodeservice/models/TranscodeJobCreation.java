package eu.kaesebrot.transcodeservice.models;

import java.io.Serializable;

public class TranscodeJobCreation implements Serializable {
    private String InFile;

    private String OutFolder;
    private long PresetId;

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

    public long getPresetId() {
        return PresetId;
    }

    public void setPresetId(long presetId) {
        PresetId = presetId;
    }

    public TranscodeJobCreation(String inFile, String outFolder, long presetId) {
        InFile = inFile;
        OutFolder = outFolder;
        PresetId = presetId;
    }
}
