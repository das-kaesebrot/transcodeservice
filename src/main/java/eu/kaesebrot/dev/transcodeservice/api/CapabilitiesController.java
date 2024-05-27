package eu.kaesebrot.dev.transcodeservice.api;

import eu.kaesebrot.dev.transcodeservice.models.rest.FFmpegPaths;
import eu.kaesebrot.dev.transcodeservice.models.rest.SupportedFormats;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transcodeservice")
@Tag(name = "capabilities", description = "The capabilities API")
public class CapabilitiesController {
    @GetMapping(
            value = "formats",
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.OK)
    public SupportedFormats GetSupportedFormats() {
        return new SupportedFormats();
    }

    @GetMapping(
            value = "paths",
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.OK)
    public FFmpegPaths GetFFmpegPaths() {
        return new FFmpegPaths();
    }
}
