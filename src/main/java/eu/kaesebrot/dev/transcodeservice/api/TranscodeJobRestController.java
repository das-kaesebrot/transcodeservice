package eu.kaesebrot.dev.transcodeservice.api;

import eu.kaesebrot.dev.transcodeservice.constants.StatusPutRequest;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodeJobCreation;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodeJobUpdate;
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
    private final TranscodeJobService jobService;

    private final TranscodePresetService presetService;

    public TranscodeJobRestController(TranscodeJobService jobService, TranscodePresetService presetService) {
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
        return jobService.insertJob(job);
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
        // TODO
        return null;
    }

    @GetMapping(
            value = "jobs/{id}/status",
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.OK)
    public String GetStatus(@PathVariable Long id) {
        return jobService.getJob(id).getStatus().name();
    }
}
