package eu.kaesebrot.dev.transcodeservice.models.rest;

import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import eu.kaesebrot.dev.transcodeservice.services.TranscodePresetService;

import java.io.Serializable;
import java.util.Optional;

public class TranscodeJobUpdate implements Serializable {
    private TranscodePresetService presetService;

    private Optional<String> InFile;

    private Optional<String> OutFolder;
    private Optional<TranscodePreset> Preset;

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

    public Optional<TranscodePreset> getPreset() {
        return Preset;
    }

    public void setPreset(Optional<Long> presetId) {
        if (presetId.isPresent()) {
            Preset = presetService.GetPresetOptional(presetId.get());
        }
    }

    public TranscodeJobUpdate(Optional<String> inFile, Optional<String> outFolder, Optional<Long> presetId) {
        InFile = inFile;
        OutFolder = outFolder;
        if (presetId.isPresent()) {
            Preset = presetService.GetPresetOptional(presetId.get());
        } else {
            Preset = Optional.empty();
        }
    }
}
