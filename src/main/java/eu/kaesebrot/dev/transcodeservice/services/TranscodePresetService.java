package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class TranscodePresetService implements ITranscodePresetService {

    private final ITranscodePresetRepository repository;

    private final ReadWriteLock presetLock;

    public TranscodePresetService(ITranscodePresetRepository repository) {
        presetLock = new ReentrantReadWriteLock();
        this.repository = repository;
    }

    @Override
    public Optional<TranscodePreset> GetPresetOptional(UUID id) {
        return repository
                .findById(id);
    }

    @Override
    public TranscodePreset GetPreset(UUID id) throws EntityNotFoundException {
        return GetPresetOptional(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No TranscodePreset found by id={%s}", id)));
    }

    @Override
    public TranscodePreset InsertPreset(TranscodePreset preset) {
        if (preset == null) {
            return null;
        }

        try {
            presetLock.writeLock().lock();
            repository.saveAndFlush(preset);
        } finally {
            presetLock.writeLock().unlock();
        }

        return GetPreset(preset.getId());
    }
}
