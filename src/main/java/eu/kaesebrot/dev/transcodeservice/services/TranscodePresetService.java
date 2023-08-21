package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;

public interface TranscodePresetService {
    Optional<TranscodePreset> getPresetOptional(Long id);
    TranscodePreset getPreset(Long id) throws EntityNotFoundException;
    TranscodePreset insertPreset(TranscodePreset preset);
}
