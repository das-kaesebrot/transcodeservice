package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import com.github.manevolent.ffmpeg4j.FFmpeg;
import com.github.manevolent.ffmpeg4j.FFmpegException;
import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.avformat.AVInputFormat;
import org.bytedeco.ffmpeg.avformat.AVOutputFormat;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avformat;
import org.bytedeco.javacpp.Pointer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.bytedeco.ffmpeg.avcodec.AVCodecContext.FF_COMPLIANCE_STRICT;
import static org.bytedeco.ffmpeg.global.avcodec.av_codec_is_decoder;
import static org.bytedeco.ffmpeg.global.avcodec.av_codec_is_encoder;
import static org.bytedeco.ffmpeg.global.avformat.avformat_query_codec;
import static org.bytedeco.ffmpeg.global.avutil.AVMEDIA_TYPE_AUDIO;
import static org.bytedeco.ffmpeg.global.avutil.AVMEDIA_TYPE_VIDEO;

@Service
public class FfmpegUtilitiesImpl implements FfmpegUtilities {

    private List<String> videoEncoderNames;
    private List<String> audioEncoderNames;
    private List<String> videoDecoderNames;
    private List<String> audioDecoderNames;
    private List<AVOutputFormat> muxers;

    @Override
    public List<String> getSupportedVideoEncoders() {
        if (videoEncoderNames == null || videoEncoderNames.isEmpty()) {
            videoEncoderNames = iterateCodecs().stream()
                    .filter(c -> c.type() == AVMEDIA_TYPE_VIDEO)
                    .filter(c -> av_codec_is_encoder(c) != 0)
                    .map(c -> c.name().getString())
                    .toList();
        }

        return videoEncoderNames;
    }

    @Override
    public List<String> getSupportedAudioEncoders() {
        if (audioEncoderNames == null || audioEncoderNames.isEmpty()) {
            audioEncoderNames = iterateCodecs().stream()
                    .filter(c -> c.type() == AVMEDIA_TYPE_AUDIO)
                    .filter(c -> av_codec_is_encoder(c) != 0)
                    .map(c -> c.name().getString())
                    .toList();
        }

        return audioEncoderNames;
    }

    @Override
    public List<String> getSupportedVideoDecoders() {
        if (videoDecoderNames == null || videoDecoderNames.isEmpty()) {
            videoDecoderNames = iterateCodecs().stream()
                    .filter(c -> c.type() == AVMEDIA_TYPE_VIDEO)
                    .filter(c -> av_codec_is_decoder(c) != 0)
                    .map(c -> c.name().getString())
                    .toList();
        }

        return videoDecoderNames;
    }

    @Override
    public List<String> getSupportedAudioDecoders() {
        if (audioDecoderNames == null || audioDecoderNames.isEmpty()) {
            audioDecoderNames = iterateCodecs().stream()
                    .filter(c -> c.type() == AVMEDIA_TYPE_AUDIO)
                    .filter(c -> av_codec_is_decoder(c) != 0)
                    .map(c -> c.name().getString())
                    .toList();
        }

        return audioDecoderNames;
    }

    @Override
    public List<AVOutputFormat> getSupportedMuxers() {
        if (muxers == null || muxers.isEmpty()) {
            muxers = iterateMuxers().stream().toList();
        }

        return muxers;
    }

    @Override
    public boolean muxerSupportsCodec(AVOutputFormat muxer, String codecName) throws FFmpegException {
        AVCodec codec = FFmpeg.getCodecByName(codecName);
        return avformat_query_codec(muxer, codec.id(), FF_COMPLIANCE_STRICT) != 0;
    }


    /**
     * See: https://ffmpeg.org/pipermail/libav-user/2018-May/011160.html
     * @return
     */
    private static <T extends Pointer> Collection<T> iterate(Function<Pointer, T> iterateFunction) {
        Collection<T> outs = new ArrayList<>();
        try (Pointer opaque = new Pointer()) {
            T out;
            while ((out = iterateFunction.apply(opaque)) != null) {
                outs.add(out);
            }
        }
        return Collections.unmodifiableCollection(outs);
    }

    private static Collection<AVOutputFormat> iterateMuxers() {
        return iterate(avformat::av_muxer_iterate);
    }

    private static Collection<AVInputFormat> iterateDemuxers() {
        return iterate(avformat::av_demuxer_iterate);
    }

    private static Collection<AVCodec> iterateCodecs() {
        return iterate(avcodec::av_codec_iterate);
    }
}
