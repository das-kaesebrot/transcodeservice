import eu.kaesebrot.dev.transcodeservice.utils.AVUtils;
import org.bytedeco.ffmpeg.avformat.AVInputFormat;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AVUtilsTest {
    @Test
    public void testInputFileFormatParsing() {
        URL resource = AVUtilsTest.class.getResource("/gran_dillama.mp4");

        AVInputFormat format = AVUtils.getInputFormat(resource.getPath());

        assertEquals("QuickTime / MOV", format.long_name().getString());
    }
}
