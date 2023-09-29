package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodeJobUpdate;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TranscodeJobService
{
    public Optional<TranscodeJob> getJobOptional(Long id);
    public TranscodeJob getJob(Long id) throws EntityNotFoundException;
    public Page<TranscodeJob> getAllJobsPaged(Pageable pageable);
    public TranscodeJob insertJob(TranscodeJob transcodeJob);
    public TranscodeJob updateJob(TranscodeJob transcodeJob);
    public TranscodeJob updateJob(TranscodeJobUpdate updateData, Long jobId);
    void setJobStatus(TranscodeJob job, ETranscodeServiceStatus status);
    public void deleteJobById(Long id);
    public void deleteByStatusList(List<ETranscodeServiceStatus> statusList);
}
