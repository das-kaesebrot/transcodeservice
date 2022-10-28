package eu.kaesebrot.transcodeservice.services;

import eu.kaesebrot.transcodeservice.models.TranscodePreset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class TranscodePresetService implements ITranscodePresetService {

    @Autowired
    private ITranscodePresetRepository repository;

    private final ReadWriteLock presetLock;

    public TranscodePresetService() {
        presetLock = new ReentrantReadWriteLock();
    }

    @Override
    public Optional<TranscodePreset> GetPresetOptional(long id) {
        return repository
                .findById(id);
    }

    @Override
    public TranscodePreset GetPreset(long id) throws EntityNotFoundException {
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
