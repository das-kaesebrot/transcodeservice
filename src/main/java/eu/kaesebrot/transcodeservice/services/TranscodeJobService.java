package eu.kaesebrot.transcodeservice.services;

import eu.kaesebrot.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.transcodeservice.models.TranscodeJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TranscodeJobService implements ITranscodeJobService
{
    private final TranscodeJobRepository repository;

    public TranscodeJobService(TranscodeJobRepository transcodeJobRepository) {
        this.repository = transcodeJobRepository;
    }

    @Override
    public TranscodeJob InsertJob(TranscodeJob transcodeJob) {
        if (transcodeJob != null) {
            repository.save(transcodeJob);
            var updatedJob = repository.findById(transcodeJob.getId());
            if (updatedJob.isPresent()) {
                return updatedJob.get();
            }
        }
        return null;
    }

    @Override
    public Optional<TranscodeJob> GetJob(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Page<TranscodeJob> GetAllJobsPaged(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void DeleteJob(TranscodeJob transcodeJob) {
        repository.delete(transcodeJob);
    }

    @Override
    public void DeleteJobById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void DeleteAllFailedJobs() {
        repository.deleteAll(
                repository
                        .findAll()
                        .stream()
                        .filter(t -> t.getTranscodeStatus().getStatusEnum() == ETranscodeServiceStatus.FAILED)
                        .toList()
            );
    }

    @Override
    public void DeleteByStatusList(List<ETranscodeServiceStatus> statusList) {
        // TODO
    }

    @Override
    public List<TranscodeJob> GetJobsByStatus(ETranscodeServiceStatus status) {
        return repository
                .findAll()
                .stream()
                .filter(t -> t.getTranscodeStatus().getStatusEnum() == status)
                .toList();
    }

    @Override
    public List<TranscodeJob> GetJobsUsingPresetId(UUID presetId) {
        return repository
                .findAll()
                .stream()
                .filter(t -> t.getPreset().getId() == presetId)
                .toList();
    }
}
