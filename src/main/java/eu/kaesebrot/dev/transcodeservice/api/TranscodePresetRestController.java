package eu.kaesebrot.dev.transcodeservice.api;

import eu.kaesebrot.dev.transcodeservice.ffmpeg.FfmpegUtilities;
import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import eu.kaesebrot.dev.transcodeservice.models.rest.SupportedFormats;
import eu.kaesebrot.dev.transcodeservice.services.TranscodePresetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transcodeservice")
@Tag(name = "preset", description = "The TranscodePreset API")
public class TranscodePresetRestController {
    private final TranscodePresetService presetService;
    private final FfmpegUtilities ffmpegUtilities;

    public TranscodePresetRestController(TranscodePresetService presetService, FfmpegUtilities ffmpegUtilities) {
        this.presetService = presetService;
        this.ffmpegUtilities = ffmpegUtilities;
    }

    @PostMapping(
            value = "presets",
            consumes = { "application/json", "application/xml" },
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public TranscodePreset CreateNewPreset() {
        return presetService.insertPreset(new TranscodePreset());
    }

    @GetMapping(
            value = "formats",
            produces = { "application/json", "application/xml" }
    )
    @ResponseStatus(HttpStatus.OK)
    public SupportedFormats GetSupportedFormates() {
        var supportedFormats = new SupportedFormats();

        supportedFormats.setSupportedVideoEncoders(ffmpegUtilities.getSupportedVideoEncoders());
        supportedFormats.setSupportedAudioEncoders(ffmpegUtilities.getSupportedAudioEncoders());
        supportedFormats.setSupportedMuxerNames(ffmpegUtilities.getSupportedMuxers().stream().map(m -> m.name().getString()).toList());

        return supportedFormats;
    }
}
