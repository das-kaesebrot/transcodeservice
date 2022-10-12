package eu.kaesebrot.transcodeservice.services;

import eu.kaesebrot.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.transcodeservice.models.TranscodeJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITranscodeJobService
{

    public TranscodeJob InsertJob(TranscodeJob transcodeJob);

    public Optional<TranscodeJob> GetJob(UUID id);

    public Page<TranscodeJob> GetAllJobsPaged(Pageable pageable);

    public void DeleteJob(TranscodeJob transcodeJob);

    public void DeleteJobById(UUID id);

    public void DeleteAllFailedJobs();

    public void DeleteByStatusList(List<ETranscodeServiceStatus> statusList);

    public List<TranscodeJob> GetJobsByStatus(ETranscodeServiceStatus status);

    public List<TranscodeJob> GetJobsUsingPresetId(UUID presetId);
}
