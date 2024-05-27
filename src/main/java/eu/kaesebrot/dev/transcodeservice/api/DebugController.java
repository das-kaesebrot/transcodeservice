package eu.kaesebrot.dev.transcodeservice.api;


import eu.kaesebrot.dev.transcodeservice.models.rest.PingResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transcodeservice")
@Tag(name = "debug", description = "Debugging API")
public class DebugController {
    @GetMapping(
            value = "ping",
            produces = { "application/json", "application/xml" }
    )
    public PingResponse Ping() {
        return new PingResponse();
    }
}
