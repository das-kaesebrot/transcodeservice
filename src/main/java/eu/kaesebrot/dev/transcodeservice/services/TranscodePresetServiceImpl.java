package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodePresetUpdate;
import eu.kaesebrot.dev.transcodeservice.utils.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@Transactional
public class TranscodePresetServiceImpl implements TranscodePresetService {

    private final TranscodePresetRepository repository;

    private final ReadWriteLock presetLock;

    public TranscodePresetServiceImpl(TranscodePresetRepository repository) {
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
    public Page<TranscodePreset> getAllPaged(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public TranscodePreset insertPreset(@NotNull TranscodePreset preset) {
        return repository.saveAndFlush(preset);
    }

    @Override
    public TranscodePreset updatePreset(TranscodePresetUpdate presetUpdate, Long id) {
        var preset = getPreset(id);

        if (!StringUtils.isNullOrEmpty(presetUpdate.getDescription())) {
            preset.setDescription(presetUpdate.getDescription());
        }

        if (!StringUtils.isNullOrEmpty(presetUpdate.getMuxer())) {
            preset.setMuxer(presetUpdate.getMuxer());
        }

        if (!presetUpdate.getTrackPresets().isEmpty()) {
            preset.setTrackPresets(presetUpdate.getTrackPresets());
        }

        return updatePreset(preset);
    }

    @Override
    public TranscodePreset updatePreset(TranscodePreset preset) {
        getPreset(preset.getId());
        return insertPreset(preset);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
