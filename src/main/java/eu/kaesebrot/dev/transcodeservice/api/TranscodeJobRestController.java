package eu.kaesebrot.dev.transcodeservice.api;

import eu.kaesebrot.dev.transcodeservice.constants.StatusPutRequest;
import eu.kaesebrot.dev.transcodeservice.ffmpeg.JobHandlerService;
import eu.kaesebrot.dev.transcodeservice.models.rest.PingResponse;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodeJobCreation;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodeJobUpdate;
import eu.kaesebrot.dev.transcodeservice.services.TranscodeJobRepository;
import eu.kaesebrot.dev.transcodeservice.services.TranscodeJobService;
import eu.kaesebrot.dev.transcodeservice.services.TranscodePresetService;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transcodeservice")
@Tag(name = "job", description = "The TranscodeJob API")
public class TranscodeJobRestController {
    private final TranscodeJobRepository jobRepository;
    private final TranscodeJobService jobService;
    private final TranscodePresetService presetService;
    private final JobHandlerService jobHandlerService;

    public TranscodeJobRestController(TranscodeJobRepository jobRepository, TranscodeJobService jobService, TranscodePresetService presetService, JobHandlerService jobHandlerService) {
        this.jobRepository = jobRepository;
        this.jobService = jobService;
        this.presetService = presetService;
        this.jobHandlerService = jobHandlerService;
    }

    @GetMapping(
            value = "ping",
            produces = { "application/json", "application/xml" }
    )
    public PingResponse Ping() {
        return new PingResponse();
    }

    @GetMapping(
            value = "jobs",
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.OK)
    public Page<TranscodeJob> GetAllJobs(Pageable pageable) {
        return jobService.getAllJobsPaged(pageable);
    }

    @PostMapping(
            value = "jobs",
            consumes = { "application/json", "application/xml" },
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public TranscodeJob CreateNewJob(@RequestBody TranscodeJobCreation jobData) {
        var preset = presetService.getPreset(jobData.getPresetId());
        var job = new TranscodeJob(jobData.getInFile(), jobData.getOutFolder(), preset);
        job = jobService.insertJob(job);

        if (jobData.enqueueImmeditely())
            jobHandlerService.submit(job);

        return jobService.getJob(job.getId());
    }

    @GetMapping(
            value = "jobs/{id}",
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.OK)
    public TranscodeJob GetJob(@PathVariable Long id) {
        return jobService.getJob(id);
    }

    @PatchMapping(
            value = "jobs/{id}",
            consumes = { "application/json", "application/xml" },
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.OK)
    public TranscodeJob UpdateJob(@PathVariable Long id, TranscodeJobUpdate transcodeJobUpdate) {
        return jobService.updateJob(transcodeJobUpdate, id);
    }

    @PutMapping(
            value = "jobs/{id}/status",
            consumes = { "application/json", "application/xml" },
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.OK)
    public TranscodeJob ChangeStatus(@PathVariable Long id, @RequestBody StatusPutRequest status) {
        if (status.equals(StatusPutRequest.START)) {
            jobHandlerService.submit(id);
        }
        else if (status.equals(StatusPutRequest.ABORT)) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        return jobService.getJob(id);
    }

    @GetMapping(
            value = "jobs/{id}/status",
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.OK)
    public String GetStatus(@PathVariable Long id) {
        return jobRepository.getStatus(id).name();
    }

    @GetMapping(
            value = "jobs/{id}/progress",
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.OK)
    public double GetProgress(@PathVariable Long id) {
        return jobHandlerService.getProgress(jobRepository.getReferenceById(id));
    }
}
