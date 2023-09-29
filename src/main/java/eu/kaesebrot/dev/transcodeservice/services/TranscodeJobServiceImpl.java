package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodeJobUpdate;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@Transactional
public class TranscodeJobServiceImpl implements TranscodeJobService
{
    private final ITranscodeJobRepository jobRepository;
    private final ITranscodePresetRepository presetRepository;
    private final ReadWriteLock jobLock;

    public TranscodeJobServiceImpl(ITranscodeJobRepository jobRepository, ITranscodePresetRepository presetRepository) {
        jobLock = new ReentrantReadWriteLock();
        this.jobRepository = jobRepository;
        this.presetRepository = presetRepository;
    }

    @Override
    public TranscodeJob insertJob(TranscodeJob transcodeJob) {
        if (transcodeJob != null) {
            try {
                jobLock.writeLock().lock();
                jobRepository.saveAndFlush(transcodeJob);
            } finally {
                jobLock.writeLock().unlock();
            }

            var updatedJob = jobRepository.findById(transcodeJob.getId());
            if (updatedJob.isPresent()) {
                return updatedJob.get();
            }
        }
        return null;
    }

    @Override
    public TranscodeJob updateJob(TranscodeJob transcodeJob) {
        getJob(transcodeJob.getId());
        return insertJob(transcodeJob);
    }

    @Override
    public TranscodeJob updateJob(TranscodeJobUpdate updateData, Long jobId) {
        var job = getJob(jobId);

        if (!StringUtils.isBlank(updateData.getInFile())) {
            job.setInFile(updateData.getInFile());
        }
        if (!StringUtils.isBlank(updateData.getOutFolder())) {
            job.setOutFolder(updateData.getOutFolder());
        }
        if (updateData.getPresetId() != null) {
            job.setPreset(presetRepository.findById(updateData.getPresetId()).get());
        }

        return updateJob(job);
    }

    @Override
    public void setJobStatus(Long jobId, ETranscodeServiceStatus status) {
        setJobStatus(getJob(jobId), status);
    }

    @Override
    public void setJobStatus(@NotNull TranscodeJob job, ETranscodeServiceStatus status) {
        try {
            jobLock.writeLock().lock();

            jobRepository.updateStatus(job.getId(), status);
        } finally {
            jobLock.writeLock().unlock();
        }
    }

    @Override
    public Optional<TranscodeJob> getJobOptional(Long id) {
        return jobRepository
                .findById(id);
    }

    @Override
    public TranscodeJob getJob(Long id) throws EntityNotFoundException {
        return getJobOptional(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No TranscodeJob found by id={%s}", id)));
    }

    @Override
    public Page<TranscodeJob> getAllJobsPaged(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }

    @Override
    public void deleteJobById(Long id) {
        jobRepository.deleteById(id);
    }

    @Override
    public void deleteByStatusList(List<ETranscodeServiceStatus> statusList) {
        jobRepository.deleteAllByStatusIn(statusList);
    }
}
