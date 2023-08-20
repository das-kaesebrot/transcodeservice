package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

public interface TranscodePresetService {
    Optional<TranscodePreset> GetPresetOptional(Long id);
    TranscodePreset GetPreset(Long id) throws EntityNotFoundException;
    TranscodePreset InsertPreset(TranscodePreset preset);
}
