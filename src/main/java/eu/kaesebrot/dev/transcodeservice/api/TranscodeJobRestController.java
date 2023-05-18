package eu.kaesebrot.dev.transcodeservice.api;

import eu.kaesebrot.dev.transcodeservice.models.TranscodeJobCreation;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJobUpdate;
import eu.kaesebrot.dev.transcodeservice.services.ITranscodeJobService;
import eu.kaesebrot.dev.transcodeservice.services.ITranscodePresetService;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transcodeservice")
@Tag(name = "job", description = "The TranscodeJob API")
public class TranscodeJobRestController {
    private final ITranscodeJobService jobService;

    private final ITranscodePresetService presetService;

    public TranscodeJobRestController(ITranscodeJobService jobService, ITranscodePresetService presetService) {
        this.jobService = jobService;
        this.presetService = presetService;
    }

    @GetMapping(
            value = "ping",
            produces = { "application/json", "application/xml" }
    )
    public String Ping() {
        return "pong";
    }

    @GetMapping(
            value = "jobs",
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.OK)
    public Page<TranscodeJob> GetAllJobs(Pageable pageable) {
        return jobService.GetAllJobsPaged(pageable);
    }

    @PostMapping(
            value = "jobs",
            consumes = { "application/json", "application/xml" },
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public TranscodeJob CreateNewJob(@RequestBody TranscodeJobCreation jobData) {
        var preset = presetService.GetPreset(jobData.getPresetId());
        var job = new TranscodeJob(jobData.getInFile(), jobData.getOutFolder(), preset);
        return jobService.InsertJob(job);
    }

    @GetMapping(
            value = "jobs/{id}",
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.OK)
    public TranscodeJob GetJob(@PathVariable UUID id) {
        return jobService.GetJob(id);
    }

    @PatchMapping(
            value = "jobs/{id}",
            consumes = { "application/json", "application/xml" },
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.OK)
    public TranscodeJob UpdateJob(@PathVariable UUID id, TranscodeJobUpdate transcodeJobUpdate) {
        return jobService.UpdateJob(transcodeJobUpdate, id);
    }
}
