package eu.kaesebrot.transcodeservice.services;

import eu.kaesebrot.transcodeservice.models.TranscodePreset;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

public interface ITranscodePresetService {
    Optional<TranscodePreset> GetPresetOptional(long id);
    TranscodePreset GetPreset(long id) throws EntityNotFoundException;
    TranscodePreset InsertPreset(TranscodePreset preset);
}
