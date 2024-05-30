import eu.kaesebrot.dev.transcodeservice.TranscodeServiceApplication;
import eu.kaesebrot.dev.transcodeservice.api.TranscodePresetRestController;
import eu.kaesebrot.dev.transcodeservice.constants.ETrackPresetType;
import eu.kaesebrot.dev.transcodeservice.models.AudioTrackPreset;
import eu.kaesebrot.dev.transcodeservice.models.TrackPreset;
import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import eu.kaesebrot.dev.transcodeservice.models.VideoTrackPreset;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodePresetCreation;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = TranscodeServiceApplication.class)
@AutoConfigureMockMvc
public class PresetApiTests {
    @Autowired
    private TranscodePresetRestController presetController;
    @Test
    @Transactional
    public void testPresetCreation() {
        // video track preset
        Double framerate = 25.0;
        String videoBitrate = "10m";
        Integer width = 1920;
        Integer height = 1080;
        String videoCodec = "libx264";
        String pixelFormat = "yuv420p";

        VideoTrackPreset videoTrackPreset = new VideoTrackPreset();
        videoTrackPreset.setFramerate(framerate);
        videoTrackPreset.setVideoBitrate(videoBitrate);
        videoTrackPreset.setWidth(width);
        videoTrackPreset.setHeight(height);
        videoTrackPreset.setVideoCodecName(videoCodec);
        videoTrackPreset.setVideoPixelFormat(pixelFormat);

        // audio track preset
        String audioBitrate = "192k";
        String audioCodec = "aac";
        Integer audioSampleRate = 44100;

        AudioTrackPreset audioTrackPreset = new AudioTrackPreset();
        audioTrackPreset.setAudioBitrate(audioBitrate);
        audioTrackPreset.setAudioCodecName(audioCodec);
        audioTrackPreset.setAudioSampleRate(audioSampleRate);

        // the preset creation model itself
        String description = "h264 testing preset";
        String muxer = "mp4";

        TranscodePresetCreation creationPreset = new TranscodePresetCreation();

        creationPreset.setDescription(description);
        creationPreset.setMuxer(muxer);
        creationPreset.setTrackPresets(Set.of(videoTrackPreset, audioTrackPreset));

        TranscodePreset resp = presetController.CreateNewPreset(creationPreset);

        Assert.assertEquals(description, resp.getDescription());
        Assert.assertEquals(muxer, resp.getMuxer());
        Assert.assertNotNull(resp.getCreatedAt());
        Assert.assertNotNull(resp.getModifiedAt());
        Assert.assertNotNull(resp.getId());

        Set<TrackPreset> respTrackPresets = resp.getTrackPresets();
        VideoTrackPreset respVideoTrackPreset = (VideoTrackPreset) respTrackPresets.stream().filter(s -> s.getType() == ETrackPresetType.VIDEO).findFirst().orElseThrow();
        AudioTrackPreset respAudioTrackPreset = (AudioTrackPreset) respTrackPresets.stream().filter(s -> s.getType() == ETrackPresetType.AUDIO).findFirst().orElseThrow();

        Assert.assertEquals(framerate, respVideoTrackPreset.getFramerate());
        Assert.assertEquals(videoBitrate, respVideoTrackPreset.getVideoBitrate());
        Assert.assertEquals(width, respVideoTrackPreset.getWidth());
        Assert.assertEquals(height, respVideoTrackPreset.getHeight());
        Assert.assertEquals(videoCodec, respVideoTrackPreset.getVideoCodecName());
        Assert.assertEquals(pixelFormat, respVideoTrackPreset.getVideoPixelFormat());
        Assert.assertTrue(respVideoTrackPreset.getVideoOptions() == null || respVideoTrackPreset.getVideoOptions().isEmpty());

        Assert.assertEquals(audioBitrate, respAudioTrackPreset.getAudioBitrate());
        Assert.assertEquals(audioCodec, respAudioTrackPreset.getAudioCodecName());
        Assert.assertEquals(audioSampleRate, respAudioTrackPreset.getAudioSampleRate());
        Assert.assertTrue(respAudioTrackPreset.getAudioOptions() == null || respAudioTrackPreset.getAudioOptions().isEmpty());
    }
}
