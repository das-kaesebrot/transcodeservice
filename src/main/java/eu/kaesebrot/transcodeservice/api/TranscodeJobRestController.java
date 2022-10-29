package eu.kaesebrot.transcodeservice.api;

import eu.kaesebrot.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.transcodeservice.models.TranscodeJobCreation;
import eu.kaesebrot.transcodeservice.models.TranscodeJobUpdate;
import eu.kaesebrot.transcodeservice.services.ITranscodeJobService;
import eu.kaesebrot.transcodeservice.services.ITranscodePresetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transcodeservice")
@Tag(name = "job", description = "The TranscodeJob API")
public class TranscodeJobRestController {
    @Autowired
    private ITranscodeJobService jobService;

    @Autowired
    private ITranscodePresetService presetService;

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
