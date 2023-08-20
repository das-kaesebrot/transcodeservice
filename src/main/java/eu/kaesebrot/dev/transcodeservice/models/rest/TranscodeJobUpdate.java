package eu.kaesebrot.dev.transcodeservice.models.rest;

import java.io.Serializable;
import java.util.Optional;

public class TranscodeJobUpdate implements Serializable {
    private Optional<String> inFile;

    private Optional<String> outFolder;
    private Optional<Long> presetId;

    public Optional<String> getInFile() {
        return inFile;
    }

    public void setInFile(Optional<String> inFile) {
        this.inFile = inFile;
    }
    public Optional<String> getOutFolder() {
        return outFolder;
    }

    public void setOutFolder(Optional<String> outFolder) {
        this.outFolder = outFolder;
    }

    public Optional<Long> getPresetId() {
        return presetId;
    }

    public void setPresetId(Optional<Long> presetId) {

    }

    public TranscodeJobUpdate(Optional<String> inFile, Optional<String> outFolder, Optional<Long> presetId) {
        this.inFile = inFile;
        this.outFolder = outFolder;
        this.presetId = presetId;
    }
}
