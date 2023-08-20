package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodeJobUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

public interface ITranscodeJobService
{
    public Optional<TranscodeJob> GetJobOptional(Long id);
    public TranscodeJob GetJob(Long id) throws EntityNotFoundException;
    public Page<TranscodeJob> GetAllJobsPaged(Pageable pageable);
    public TranscodeJob InsertJob(TranscodeJob transcodeJob);
    public TranscodeJob UpdateJob(TranscodeJob transcodeJob);
    public TranscodeJob UpdateJob(TranscodeJobUpdate updateData, Long jobId);
    public void DeleteJobById(Long id);
    public void DeleteByStatusList(List<ETranscodeServiceStatus> statusList);
}
