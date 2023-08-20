package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.info.Codec;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class FfmpegUtilitiesImpl implements FfmpegUtilities {

    @Override
    public List<String> getSupportedVideoCodecs() throws IOException {
        // TODO

        /*
        FFmpeg fFmpeg = new FFmpeg("/usr/bin/ffmpeg");

        var codecs = fFmpeg
                .codecs()
                .stream()
                .filter(Codec::getCanEncode)
                .filter()
                .findFirst().get();

        return codecs;
        */

        return null;
    }

    @Override
    public List<String> getSupportedAudioCodecs() {
        return null;
    }
}
