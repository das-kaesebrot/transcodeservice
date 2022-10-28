package eu.kaesebrot.transcodeservice.services;

import eu.kaesebrot.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.transcodeservice.models.TranscodeJobUpdate;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Dictionary;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

@Service
public class TranscodeJobService implements ITranscodeJobService
{
    @Autowired
    private ITranscodeJobRepository repository;


    private final ReadWriteLock jobLock;

    public TranscodeJobService() {
        jobLock = new ReentrantReadWriteLock();
    }

    @Override
    public TranscodeJob InsertJob(TranscodeJob transcodeJob) {
        if (transcodeJob != null) {

            try {
                jobLock.writeLock().lock();
                repository.save(transcodeJob);
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
    public TranscodeJob UpdateJob(Dictionary<String, Object> updateData, UUID jobId) {
        throw new NotYetImplementedException();
    }

    @Override
    public TranscodeJob UpdateJob(TranscodeJobUpdate updateData, UUID jobId) {
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
    public long CountAllJobs() {
        return repository.count();
    }

    @Override
    public long CountSuccessfulJobs() {
        return CountJobsWithStatusList(List.of(ETranscodeServiceStatus.SUCCESS));
    }

    @Override
    public long CountFailedJobs() {
        return CountJobsWithStatusList(List.of(ETranscodeServiceStatus.FAILED));
    }

    @Override
    public long CountJobsWithStatusList(List<ETranscodeServiceStatus> statusList) {
        return
                GetJobStreamByStatusList(statusList)
                        .count();
    }

    @Override
    public Optional<TranscodeJob> GetJobOptional(UUID id) {
        return repository
                .findById(id);
    }

    @Override
    public TranscodeJob GetJob(UUID id) throws EntityNotFoundException {
        return GetJobOptional(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No TranscodeJob found by id={%s}", id)));
    }

    @Override
    public Page<TranscodeJob> GetAllJobsPaged(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<TranscodeJob> GetRunningJobs() {
        return GetJobsByStatusList(List.of(ETranscodeServiceStatus.RUNNING));
    }

    @Override
    public void DeleteJob(TranscodeJob transcodeJob) {
        if (transcodeJob != null) {
            DeleteJobById(transcodeJob.getId());
        }
    }

    @Override
    public void DeleteJobById(UUID id) {
        try {
            jobLock.writeLock().lock();

            repository.deleteById(id);
        } finally {
            jobLock.writeLock().unlock();
        }
    }

    @Override
    public void DeleteAllFailedJobs() {
        DeleteByStatusList(List.of(ETranscodeServiceStatus.FAILED));
    }

    @Override
    public void DeleteByStatusList(List<ETranscodeServiceStatus> statusList) {
        var result = GetJobsByStatusList(statusList);

        try {
            jobLock.writeLock().lock();
            repository.deleteAll(result);
        } finally {
            jobLock.writeLock().unlock();
        }
    }

    @Override
    public List<TranscodeJob> GetJobsByStatusList(List<ETranscodeServiceStatus> statusList) throws EntityNotFoundException {
        var result = repository
                .findAll()
                .stream()
                .filter(t -> statusList.contains(t.getTranscodeStatus().getStatusEnum()))
                .sorted()
                .toList();

        if (result.isEmpty()) {
            throw new EntityNotFoundException(String.format("Can't find any jobs with status: %s", statusList));
        }

        return result;
    }

    @Override
    public Stream<TranscodeJob> GetJobStreamByStatusList(List<ETranscodeServiceStatus> statusList) throws EntityNotFoundException {
        var result = repository
                .findAll()
                .stream()
                .filter(t -> statusList.contains(t.getTranscodeStatus().getStatusEnum()))
                .sorted();

        if (result.findAny().isEmpty()) {
            throw new EntityNotFoundException(String.format("Can't find any jobs with status: %s", statusList));
        }

        return result;
    }

    @Override
    public List<TranscodeJob> GetJobsUsingPresetId(long presetId) {
        return repository
                .findAll()
                .stream()
                .filter(t -> t.getPreset().getId() == presetId)
                .sorted()
                .toList();
    }
}
