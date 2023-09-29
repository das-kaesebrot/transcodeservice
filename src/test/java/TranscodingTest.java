import eu.kaesebrot.dev.transcodeservice.TranscodeServiceApplication;
import eu.kaesebrot.dev.transcodeservice.api.TranscodeJobRestController;
import eu.kaesebrot.dev.transcodeservice.api.TranscodePresetRestController;
import eu.kaesebrot.dev.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.dev.transcodeservice.models.AudioTrackPreset;
import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import eu.kaesebrot.dev.transcodeservice.models.VideoTrackPreset;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodeJobCreation;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodePresetCreation;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = TranscodeServiceApplication.class)
@AutoConfigureMockMvc
public class TranscodingTest {
    @Autowired
    private TranscodeJobRestController jobController;
    @Autowired
    private TranscodePresetRestController presetController;
    @Test
    @Transactional
    public void testTranscoding() throws InterruptedException {
        URL resource = AVUtilsTest.class.getResource("/gran_dillama.mp4");

        var preset = new TranscodePresetCreation();
        preset.setDescription("Unit testing preset");
        preset.setMuxer("mp4");

        var videoTrackPreset = new VideoTrackPreset();
        videoTrackPreset.setVideoCodecName("libopenh264");
        videoTrackPreset.setWidth(1920);
        videoTrackPreset.setHeight(1080);
        videoTrackPreset.setFramerate(25.0);
        videoTrackPreset.setVideoPixelFormat("yuv420p");

        var audioTrackPreset = new AudioTrackPreset();
        audioTrackPreset.setAudioCodecName("aac");
        audioTrackPreset.setAudioBitrate("192kbps");
        audioTrackPreset.setAudioSampleRate(44100);

        preset.setTrackPresets(Set.of(videoTrackPreset, audioTrackPreset));

        TranscodePreset createdPreset = assertDoesNotThrow(() -> presetController.CreateNewPreset(preset));

        var presetId = createdPreset.getId();
        assertEquals(1, presetId);

        var outputFolder = Files.newTemporaryFolder();

        var transcodeRequest = new TranscodeJobCreation();
        transcodeRequest.setEnqueueImmediately(true);
        transcodeRequest.setInFile(resource.getPath());
        transcodeRequest.setOutFolder(outputFolder.getAbsolutePath());
        transcodeRequest.setPresetId(presetId);

        var job = jobController.CreateNewJob(transcodeRequest);

        ETranscodeServiceStatus status = null;

        while (true) {
            status = ETranscodeServiceStatus.valueOf(jobController.GetStatus(job.getId()));
            if (status == ETranscodeServiceStatus.FAILED || status == ETranscodeServiceStatus.SUCCESS || status == ETranscodeServiceStatus.ABORTED) {
                break;
            }

            Logger.getLogger("test").info(String.format("status is %s", status.name()));

            TimeUnit.SECONDS.sleep(1);
        }

        assertEquals(ETranscodeServiceStatus.SUCCESS, status);
    }
}
