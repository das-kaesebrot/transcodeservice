package eu.kaesebrot.dev.transcodeservice.api;

import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import eu.kaesebrot.dev.transcodeservice.services.ITranscodePresetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transcodeservice")
@Tag(name = "preset", description = "The TranscodePreset API")
public class TranscodePresetRestController {
    @Autowired
    private ITranscodePresetService presetService;

    @PostMapping(
            value = "presets",
            consumes = { "application/json", "application/xml" },
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public TranscodePreset CreateNewPreset() {
        return presetService.InsertPreset(new TranscodePreset());
    }
}
