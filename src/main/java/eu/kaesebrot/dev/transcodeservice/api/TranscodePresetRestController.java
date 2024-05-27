package eu.kaesebrot.dev.transcodeservice.api;

import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodePresetCreation;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodePresetUpdate;
import eu.kaesebrot.dev.transcodeservice.services.TranscodePresetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        return presetService.insertPreset(presetCreation.generateNewPreset());
    }

    @PatchMapping(
            value = "presets/{id}",
            consumes = { "application/json", "application/xml" },
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.OK)
    public TranscodePreset UpdatePreset(@PathVariable Long id, @RequestBody TranscodePresetUpdate presetUpdate) {
        return presetService.updatePreset(presetUpdate, id);
    }

    @GetMapping(
            value = "presets/{id}",
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.OK)
    public TranscodePreset GetPreset(@PathVariable Long id) {
        return presetService.getPreset(id);
    }


    @GetMapping(
            value = "presets",
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.OK)
    public Page<TranscodePreset> GetAllPresets(Pageable pageable) {
        return presetService.getAllPaged(pageable);
    }

    @DeleteMapping(
            value = "presets/{id}",
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void DeletePreset(@PathVariable Long id) {
        presetService.deleteById(id);
    }
}
