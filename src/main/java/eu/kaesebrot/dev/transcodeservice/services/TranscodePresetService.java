package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodePresetUpdate;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TranscodePresetService {
    Optional<TranscodePreset> getPresetOptional(Long id);
    TranscodePreset getPreset(Long id) throws EntityNotFoundException;
    public Page<TranscodePreset> getAllPaged(Pageable pageable);
    TranscodePreset insertPreset(TranscodePreset preset);
    TranscodePreset updatePreset(TranscodePreset preset);
    TranscodePreset updatePreset(TranscodePresetUpdate presetUpdate, Long id);
    void deleteById(Long id);
}
