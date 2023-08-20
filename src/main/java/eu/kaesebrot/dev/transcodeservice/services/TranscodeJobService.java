package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodeJobUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
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
    public void deleteJobById(Long id);
    public void deleteByStatusList(List<ETranscodeServiceStatus> statusList);
}
