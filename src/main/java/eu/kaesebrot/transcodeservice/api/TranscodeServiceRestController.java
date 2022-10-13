package eu.kaesebrot.transcodeservice.api;

import eu.kaesebrot.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.transcodeservice.services.ITranscodeJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
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

}
