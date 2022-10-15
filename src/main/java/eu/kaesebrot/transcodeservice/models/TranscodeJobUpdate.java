package eu.kaesebrot.transcodeservice.models;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

public class TranscodeJobUpdate implements Serializable {

    private Optional<String> InFile;

    private Optional<String> OutFolder;

    private Optional<UUID> PresetId;

    public Optional<String> getInFile() {
        return InFile;
    }

    public void setInFile(Optional<String> inFile) {
        InFile = inFile;
    }
    public Optional<String> getOutFolder() {
        return OutFolder;
    }

    public void setOutFolder(Optional<String> outFolder) {
        OutFolder = outFolder;
    }

    public Optional<UUID> getPresetId() {
        return PresetId;
    }

    public void setPresetId(Optional<UUID> presetId) {
        PresetId = presetId;
    }

    public TranscodeJobUpdate(Optional<String> inFile, Optional<String> outFolder, Optional<UUID> presetId) {
        InFile = inFile;
        OutFolder = outFolder;
        PresetId = presetId;
    }
}
