package eu.kaesebrot.dev.transcodeservice.utils;

import com.github.manevolent.ffmpeg4j.FFmpeg;
import com.github.manevolent.ffmpeg4j.FFmpegException;
import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avformat.AVInputFormat;
import org.bytedeco.ffmpeg.avformat.AVOutputFormat;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avformat;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;

import java.util.*;
import java.util.function.Function;

import static org.bytedeco.ffmpeg.avcodec.AVCodecContext.FF_COMPLIANCE_STRICT;
import static org.bytedeco.ffmpeg.global.avcodec.av_codec_is_decoder;
import static org.bytedeco.ffmpeg.global.avcodec.av_codec_is_encoder;
import static org.bytedeco.ffmpeg.global.avformat.avformat_query_codec;
import static org.bytedeco.ffmpeg.global.avutil.AVMEDIA_TYPE_AUDIO;
import static org.bytedeco.ffmpeg.global.avutil.AVMEDIA_TYPE_VIDEO;

public class AVUtils {
    private static List<String> videoEncoderNames;
    private static List<String> audioEncoderNames;
    private static List<String> videoDecoderNames;
    private static List<String> audioDecoderNames;
    private static List<AVOutputFormat> muxers;
    private static Collection<AVCodec> codecs;
    private static Map<String, List<Integer>> supportedAudioSampleRatesPerCodec;
    private static Map<String, List<String>> supportedVideoPixFmtsPerCodec;

    public static AVInputFormat getInputFormat(String filename) {
        AVFormatContext formatContext = new AVFormatContext(null);

        try {
            int ret = avformat.avformat_open_input(formatContext, filename, null, null);
            if (ret < 0) {
                throw new RuntimeException("Couldn't open given file!");
            }

            AVInputFormat inputFormat = formatContext.iformat();
            if (inputFormat == null) {
                avformat.avformat_close_input(formatContext);
                throw new RuntimeException("Format probing failed!");
            }

            return inputFormat;

        } finally {
            avformat.avformat_close_input(formatContext);
        }
    }

    public static List<Integer> getSupportedAudioSampleRatesForCodec(String codecName) {
        try {
            return getSupportedAudioSampleRatesForCodec(FFmpeg.getCodecByName(codecName));
        } catch (FFmpegException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Integer> getSupportedAudioSampleRatesForCodec(AVCodec codec) {
        if (getSupportedAudioSampleRates().containsKey(codec.name().getString()))
            return getSupportedAudioSampleRates().get(codec.name().getString());

        return List.of();
    }

    public static List<String> getSupportedPixelFormatNamesForCodec(String codecName) {
        try {
            return getSupportedPixelFormatNamesForCodec(FFmpeg.getCodecByName(codecName));
        } catch (FFmpegException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getSupportedPixelFormatNamesForCodec(AVCodec codec) {
        if (getSupportedPixelFormatNames().containsKey(codec.name().getString()))
            return getSupportedPixelFormatNames().get(codec.name().getString());

        return List.of();
    }

    public static Map<String, List<Integer>> getSupportedAudioSampleRates() {
        if (supportedAudioSampleRatesPerCodec != null || !supportedAudioSampleRatesPerCodec.isEmpty())
            return supportedAudioSampleRatesPerCodec;

        Map<String, List<Integer>> resultMap = new TreeMap<>();

        for (var codec : iterateCodecs()) {
            if (codec.type() != AVMEDIA_TYPE_AUDIO)
                continue;

            if (codec.supported_samplerates() == null)
                continue;

            var supportedSampleRates = new HashSet<Integer>();

            IntPointer samplerates = codec.supported_samplerates();

            for (int i = 0; samplerates.get(i) != -1; i++) {
                int samplerate = samplerates.get(i);
                supportedSampleRates.add(samplerate);
            }

            resultMap.put(codec.name().getString(), supportedSampleRates.stream().sorted().toList());
        }

        supportedAudioSampleRatesPerCodec = resultMap;

        return supportedAudioSampleRatesPerCodec;
    }

    public static Map<String, List<String>> getSupportedPixelFormatNames() {
        if (supportedVideoPixFmtsPerCodec != null || !supportedVideoPixFmtsPerCodec.isEmpty())
            return supportedVideoPixFmtsPerCodec;

        Map<String, List<String>> resultMap = new TreeMap<>();

        for (var codec : iterateCodecs()) {
            if (codec.type() != AVMEDIA_TYPE_VIDEO)
                continue;

            if (codec.pix_fmts() == null)
                continue;

            var supportedPixFormatNames = new ArrayList<String>();

            IntPointer pixelFormats = codec.pix_fmts();

            for (int i = 0; pixelFormats.get(i) != -1; i++) {
                int pixelFormat = pixelFormats.get(i);
                supportedPixFormatNames.add(avutil.av_get_pix_fmt_name(pixelFormat).getString());
            }

            Collections.sort(supportedPixFormatNames);

            resultMap.put(codec.name().getString(), supportedPixFormatNames);
        }

        supportedVideoPixFmtsPerCodec = resultMap;

        return supportedVideoPixFmtsPerCodec;
    }

    public static <T extends Pointer> Collection<T> iterate(Function<Pointer, T> iterateFunction) {
        Collection<T> outs = new ArrayList<>();
        try (Pointer opaque = new Pointer()) {
            T out;
            while ((out = iterateFunction.apply(opaque)) != null) {
                outs.add(out);
            }
        }
        return Collections.unmodifiableCollection(outs);
    }

    public static List<String> getSupportedVideoEncoders() {
        if (videoEncoderNames == null || videoEncoderNames.isEmpty()) {
            videoEncoderNames = iterateCodecs().stream()
                    .filter(c -> c.type() == AVMEDIA_TYPE_VIDEO)
                    .filter(c -> av_codec_is_encoder(c) != 0)
                    .map(c -> c.name().getString())
                    .toList();
        }

        return videoEncoderNames;
    }

    public static List<String> getSupportedAudioEncoders() {
        if (audioEncoderNames == null || audioEncoderNames.isEmpty()) {
            audioEncoderNames = iterateCodecs().stream()
                    .filter(c -> c.type() == AVMEDIA_TYPE_AUDIO)
                    .filter(c -> av_codec_is_encoder(c) != 0)
                    .map(c -> c.name().getString())
                    .toList();
        }

        return audioEncoderNames;
    }

    public static List<String> getSupportedVideoDecoders() {
        if (videoDecoderNames == null || videoDecoderNames.isEmpty()) {
            videoDecoderNames = iterateCodecs().stream()
                    .filter(c -> c.type() == AVMEDIA_TYPE_VIDEO)
                    .filter(c -> av_codec_is_decoder(c) != 0)
                    .map(c -> c.name().getString())
                    .toList();
        }

        return videoDecoderNames;
    }

    public static List<String> getSupportedAudioDecoders() {
        if (audioDecoderNames == null || audioDecoderNames.isEmpty()) {
            audioDecoderNames = iterateCodecs().stream()
                    .filter(c -> c.type() == AVMEDIA_TYPE_AUDIO)
                    .filter(c -> av_codec_is_decoder(c) != 0)
                    .map(c -> c.name().getString())
                    .toList();
        }

        return audioDecoderNames;
    }

    public static List<String> getSupportedMuxerNames() {
        return getSupportedMuxers().stream().map(m -> m.name().getString()).toList();
    }

    public static List<AVOutputFormat> getSupportedMuxers() {
        if (muxers == null || muxers.isEmpty()) {
            muxers = iterateMuxers().stream().toList();
        }

        return muxers;
    }

    public static boolean muxerSupportsCodec(AVOutputFormat muxer, String codecName) throws FFmpegException {
        AVCodec codec = FFmpeg.getCodecByName(codecName);
        return avformat_query_codec(muxer, codec.id(), FF_COMPLIANCE_STRICT) != 0;
    }

    public static Collection<AVOutputFormat> iterateMuxers() {
        return iterate(avformat::av_muxer_iterate);
    }

    public static Collection<AVInputFormat> iterateDemuxers() {
        return iterate(avformat::av_demuxer_iterate);
    }

    public static Collection<AVCodec> iterateCodecs() {
        if (codecs == null || codecs.isEmpty())
        {
            codecs = iterate(avcodec::av_codec_iterate);
        }

        return codecs;
    }
}
