package eu.kaesebrot.dev.transcodeservice.api;

import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import eu.kaesebrot.dev.transcodeservice.models.rest.SupportedFormats;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodePresetCreation;
import eu.kaesebrot.dev.transcodeservice.services.TranscodePresetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transcodeservice")
@Tag(name = "preset", description = "The TranscodePreset API")
public class TranscodePresetRestController {
    private final TranscodePresetService presetService;

    public TranscodePresetRestController(TranscodePresetService presetService) {
        this.presetService = presetService;
    }

    @PostMapping(
            value = "presets",
            consumes = { "application/json", "application/xml" },
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public TranscodePreset CreateNewPreset(@RequestBody TranscodePresetCreation presetCreation) {
        // TODO
        return presetService.insertPreset(presetCreation.generateNewPreset());
    }

    @GetMapping(
            value = "formats",
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.OK)
    public SupportedFormats GetSupportedFormats() {
        return new SupportedFormats();
    }
}
