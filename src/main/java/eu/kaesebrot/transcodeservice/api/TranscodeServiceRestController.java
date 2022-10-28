package eu.kaesebrot.transcodeservice.api;

import eu.kaesebrot.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.transcodeservice.models.TranscodeJobUpdate;
import eu.kaesebrot.transcodeservice.services.ITranscodeJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transcodeservice")
public class TranscodeServiceRestController {
    @Autowired
    ITranscodeJobService jobService;

    @GetMapping(value = "ping")
    public String Ping() {
        return "pong";
    }

    @GetMapping(value = "jobs")
    public Page<TranscodeJob> GetAllJobs(Pageable pageable) {
        return jobService.GetAllJobsPaged(pageable);
    }

    @PostMapping(value = "jobs")
    public TranscodeJob CreateNewJob(TranscodeJobNew jobData) {
        var preset = presetService.GetPreset(jobData.getPresetId());
        var job = new TranscodeJob(jobData.getInFile(), jobData.getOutFolder(), preset);
        return jobService.InsertJob(job);
    }

    @GetMapping(value = "jos/{id}")
    public TranscodeJob GetJob(@PathVariable UUID id) {
        return jobService.GetJob(id);
    }

    @PatchMapping(value = "jobs/{id}")
    public TranscodeJob UpdateJob(@PathVariable UUID id, TranscodeJobUpdate transcodeJobUpdate) {
        return jobService.UpdateJob(transcodeJobUpdate, id);
    }
}
