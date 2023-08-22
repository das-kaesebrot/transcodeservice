package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class TranscodePresetServiceImpl implements TranscodePresetService {

    private final ITranscodePresetRepository repository;

    private final ReadWriteLock presetLock;

    public TranscodePresetServiceImpl(ITranscodePresetRepository repository) {
        presetLock = new ReentrantReadWriteLock();
        this.repository = repository;
    }

    @Override
    public Optional<TranscodePreset> getPresetOptional(Long id) {
        return repository
                .findById(id);
    }

    @Override
    public TranscodePreset getPreset(Long id) throws EntityNotFoundException {
        return getPresetOptional(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No TranscodePreset found by id={%s}", id)));
    }

    @Override
    public TranscodePreset insertPreset(@NotNull TranscodePreset preset) {
        return repository.saveAndFlush(preset);
    }
}
