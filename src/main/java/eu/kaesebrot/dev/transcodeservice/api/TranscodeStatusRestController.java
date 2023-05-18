package eu.kaesebrot.dev.transcodeservice.api;

import eu.kaesebrot.dev.transcodeservice.constants.StatusPutRequest;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transcodeservice")
@Tag(name = "status", description = "The TranscodeStatus API")
public class TranscodeStatusRestController {
    @PutMapping(
            value = "status/{id}",
            consumes = { "application/json", "application/xml" },
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.OK)
    public TranscodeStatus ChangeStatus(@PathVariable UUID id, @RequestBody StatusPutRequest status) {
        // TODO
        return null;
    }
}
