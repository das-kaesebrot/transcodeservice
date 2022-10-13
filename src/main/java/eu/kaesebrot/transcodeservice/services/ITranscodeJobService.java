package eu.kaesebrot.transcodeservice.services;

import eu.kaesebrot.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.transcodeservice.models.TranscodeJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.util.Dictionary;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface ITranscodeJobService
{
    public Optional<TranscodeJob> GetJobOptional(UUID id);
    public TranscodeJob GetJob(UUID id) throws EntityNotFoundException;
    public Page<TranscodeJob> GetAllJobsPaged(Pageable pageable);
    public List<TranscodeJob> GetRunningJobs();
    public List<TranscodeJob> GetJobsByStatusList(List<ETranscodeServiceStatus> statusList) throws EntityNotFoundException;
    public Stream<TranscodeJob> GetJobStreamByStatusList(List<ETranscodeServiceStatus> statusList) throws EntityNotFoundException;
    public List<TranscodeJob> GetJobsUsingPresetId(UUID presetId);
    public TranscodeJob InsertJob(TranscodeJob transcodeJob);
    public TranscodeJob UpdateJob(TranscodeJob transcodeJob);
    public TranscodeJob UpdateJob(Dictionary<String, Object> updateData, UUID jobId);
    public long CountAllJobs();
    public long CountSuccessfulJobs();
    public long CountFailedJobs();
    public long CountJobsWithStatusList(List<ETranscodeServiceStatus> statusList);
    public void DeleteJob(TranscodeJob transcodeJob);
    public void DeleteJobById(UUID id);
    public void DeleteAllFailedJobs();
    public void DeleteByStatusList(List<ETranscodeServiceStatus> statusList);
}
