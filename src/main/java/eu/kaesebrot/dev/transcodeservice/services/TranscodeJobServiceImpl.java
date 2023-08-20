package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodeJobUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class TranscodeJobServiceImpl implements ITranscodeJobService
{
    private final ITranscodeJobRepository repository;

    private final ReadWriteLock jobLock;

    public TranscodeJobServiceImpl(ITranscodeJobRepository repository) {
        jobLock = new ReentrantReadWriteLock();
        this.repository = repository;
    }

    @Override
    public TranscodeJob InsertJob(TranscodeJob transcodeJob) {
        if (transcodeJob != null) {
            try {
                jobLock.writeLock().lock();
                repository.saveAndFlush(transcodeJob);
            } finally {
                jobLock.writeLock().unlock();
            }

            var updatedJob = repository.findById(transcodeJob.getId());
            if (updatedJob.isPresent()) {
                return updatedJob.get();
            }
        }
        return null;
    }

    @Override
    public TranscodeJob UpdateJob(TranscodeJob transcodeJob) {
        GetJob(transcodeJob.getId());
        return InsertJob(transcodeJob);
    }

    @Override
    public TranscodeJob UpdateJob(TranscodeJobUpdate updateData, Long jobId) {
        var job = GetJob(jobId);

        if (updateData.getInFile().isPresent()) {
            job.setInFile(updateData.getInFile().get());
        }
        if (updateData.getOutFolder().isPresent()) {
            job.setOutFolder(updateData.getOutFolder().get());
        }
        if (updateData.getPreset().isPresent()) {
            job.setPreset(updateData.getPreset().get());
        }

        return UpdateJob(job);
    }

    @Override
    public Optional<TranscodeJob> GetJobOptional(Long id) {
        return repository
                .findById(id);
    }

    @Override
    public TranscodeJob GetJob(Long id) throws EntityNotFoundException {
        return GetJobOptional(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No TranscodeJob found by id={%s}", id)));
    }

    @Override
    public Page<TranscodeJob> GetAllJobsPaged(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void DeleteJobById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void DeleteByStatusList(List<ETranscodeServiceStatus> statusList) {
        repository.deleteAllByTranscodeStatusIn(statusList);
    }
}
