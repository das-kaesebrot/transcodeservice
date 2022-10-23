package eu.kaesebrot.transcodeservice.services;

import eu.kaesebrot.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.transcodeservice.models.TranscodePreset;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TranscodePresetService implements ITranscodePresetService {

    @Autowired
    private ITranscodePresetRepository repository;

    private final ReadWriteLock presetLock;

    public TranscodePresetService() {
        presetLock = new ReentrantReadWriteLock();
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
}