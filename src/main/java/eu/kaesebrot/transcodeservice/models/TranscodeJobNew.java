package eu.kaesebrot.transcodeservice.models;

import eu.kaesebrot.transcodeservice.services.ITranscodePresetService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

public class TranscodeJobNew implements Serializable {
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

    public TranscodeJobNew(String inFile, String outFolder, long presetId) {
        InFile = inFile;
        OutFolder = outFolder;
        PresetId = presetId;
    }
}
