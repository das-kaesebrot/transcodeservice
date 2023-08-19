package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodeJobUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.util.Dictionary;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface ITranscodeJobService
{
    public Optional<TranscodeJob> GetJobOptional(Long id);
    public TranscodeJob GetJob(Long id) throws EntityNotFoundException;
    public Page<TranscodeJob> GetAllJobsPaged(Pageable pageable);
    public List<TranscodeJob> GetRunningJobs();
    public List<TranscodeJob> GetJobsByStatusList(List<ETranscodeServiceStatus> statusList) throws EntityNotFoundException;
    public Stream<TranscodeJob> GetJobStreamByStatusList(List<ETranscodeServiceStatus> statusList) throws EntityNotFoundException;
    public List<TranscodeJob> GetJobsUsingPresetId(Long presetId);
    public TranscodeJob InsertJob(TranscodeJob transcodeJob);
    public TranscodeJob UpdateJob(TranscodeJob transcodeJob);
    public TranscodeJob UpdateJob(Dictionary<String, Object> updateData, Long jobId);
    public TranscodeJob UpdateJob(TranscodeJobUpdate updateData, Long jobId);
    public long CountAllJobs();
    public long CountSuccessfulJobs();
    public long CountFailedJobs();
    public long CountJobsWithStatusList(List<ETranscodeServiceStatus> statusList);
    public void DeleteJob(TranscodeJob transcodeJob);
    public void DeleteJobById(Long id);
    public void DeleteAllFailedJobs();
    public void DeleteByStatusList(List<ETranscodeServiceStatus> statusList);
}
