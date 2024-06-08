import eu.kaesebrot.dev.transcodeservice.TranscodeServiceApplication;
import eu.kaesebrot.dev.transcodeservice.api.TranscodePresetRestController;
import eu.kaesebrot.dev.transcodeservice.constants.ETrackPresetType;
import eu.kaesebrot.dev.transcodeservice.models.AudioTrackPreset;
import eu.kaesebrot.dev.transcodeservice.models.TrackPreset;
import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import eu.kaesebrot.dev.transcodeservice.models.VideoTrackPreset;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodePresetCreation;
import helper.DataHelper;
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
        TranscodePresetCreation creationPreset = DataHelper.getCreationPreset();
        VideoTrackPreset videoTrackPreset = DataHelper.getVideoTrackPreset();
        AudioTrackPreset audioTrackPreset = DataHelper.getAudioTrackPreset();

        TranscodePreset resp = presetController.CreateNewPreset(creationPreset);

        Assert.assertEquals(creationPreset.getDescription(), resp.getDescription());
        Assert.assertEquals(creationPreset.getMuxer(), resp.getMuxer());
        Assert.assertNotNull(resp.getCreatedAt());
        Assert.assertNotNull(resp.getModifiedAt());
        Assert.assertNotNull(resp.getId());

        Set<TrackPreset> respTrackPresets = resp.getTrackPresets();
        VideoTrackPreset respVideoTrackPreset = (VideoTrackPreset) respTrackPresets.stream().filter(s -> s.getType() == ETrackPresetType.VIDEO).findFirst().orElseThrow();
        AudioTrackPreset respAudioTrackPreset = (AudioTrackPreset) respTrackPresets.stream().filter(s -> s.getType() == ETrackPresetType.AUDIO).findFirst().orElseThrow();

        Assert.assertEquals(videoTrackPreset.getFramerate(), respVideoTrackPreset.getFramerate());
        Assert.assertEquals(videoTrackPreset.getVideoBitrate(), respVideoTrackPreset.getVideoBitrate());
        Assert.assertEquals(videoTrackPreset.getWidth(), respVideoTrackPreset.getWidth());
        Assert.assertEquals(videoTrackPreset.getHeight(), respVideoTrackPreset.getHeight());
        Assert.assertEquals(videoTrackPreset.getVideoCodecName(), respVideoTrackPreset.getVideoCodecName());
        Assert.assertEquals(videoTrackPreset.getVideoPixelFormat(), respVideoTrackPreset.getVideoPixelFormat());
        Assert.assertTrue(respVideoTrackPreset.getVideoOptions() == null || respVideoTrackPreset.getVideoOptions().isEmpty());

        Assert.assertEquals(audioTrackPreset.getAudioBitrate(), respAudioTrackPreset.getAudioBitrate());
        Assert.assertEquals(audioTrackPreset.getAudioCodecName(), respAudioTrackPreset.getAudioCodecName());
        Assert.assertEquals(audioTrackPreset.getAudioSampleRate(), respAudioTrackPreset.getAudioSampleRate());
        Assert.assertTrue(respAudioTrackPreset.getAudioOptions() == null || respAudioTrackPreset.getAudioOptions().isEmpty());
    }
}
