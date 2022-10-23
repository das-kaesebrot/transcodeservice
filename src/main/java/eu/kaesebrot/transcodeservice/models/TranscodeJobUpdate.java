package eu.kaesebrot.transcodeservice.models;

import eu.kaesebrot.transcodeservice.services.ITranscodePresetService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

public class TranscodeJobUpdate implements Serializable {
    @Autowired
    private ITranscodePresetService presetService;

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

    public void setPreset(Optional<UUID> presetId) {
        if (presetId.isPresent()) {
            Preset = presetService.GetPresetOptional(presetId.get());
        }
    }

    public TranscodeJobUpdate(Optional<String> inFile, Optional<String> outFolder, Optional<UUID> presetId) {
        InFile = inFile;
        OutFolder = outFolder;
        if (presetId.isPresent()) {
            Preset = presetService.GetPresetOptional(presetId.get());
        } else {
            Preset = Optional.empty();
        }
    }
}
