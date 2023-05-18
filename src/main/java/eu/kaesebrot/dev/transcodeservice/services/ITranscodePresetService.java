package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;

public interface ITranscodePresetService {
    Optional<TranscodePreset> GetPresetOptional(UUID id);
    TranscodePreset GetPreset(UUID id) throws EntityNotFoundException;
    TranscodePreset InsertPreset(TranscodePreset preset);
}
