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

    public static String getInputFormat(String filename) {
        AVFormatContext formatContext = new AVFormatContext(null);

        try {
            int ret = avformat.avformat_open_input(formatContext, filename, null, null);
            if (ret < 0) {
                throw new RuntimeException("Couldn't open given file!");
            }

            AVInputFormat inputFormat = avformat.av_find_input_format(formatContext.iformat().name().getString());
            if (inputFormat == null) {
                avformat.avformat_close_input(formatContext);
                throw new RuntimeException("Format probing failed!");
            }

            return inputFormat.name().getString();

        } finally {
            avformat.avformat_close_input(formatContext);
        }
    }

    public static Set<String> getSupportedAudioSampleRates(AVCodec codec) {
        if (codec.type() != AVMEDIA_TYPE_AUDIO)
            throw new IllegalArgumentException(String.format("Given codec '%s' is not an audio codec!", codec.name()));

        var supportedSampleRates = new HashSet<String>();

        var sampleRatesPointer = codec.supported_samplerates();

        // TODO does this work?
        for (int i = 0; i < sampleRatesPointer.sizeof(); i++) {
            supportedSampleRates.add(avutil.av_get_sample_fmt_name(sampleRatesPointer.get(i)).getString());
        }

        return supportedSampleRates;
    }

    public static Set<String> getSupportedPixelFormatNames(String codecName) {
        try {
            return getSupportedPixelFormatNames(FFmpeg.getCodecByName(codecName));
        } catch (FFmpegException e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<String> getSupportedPixelFormatNames(AVCodec codec) {
        if (codec.type() != AVMEDIA_TYPE_VIDEO)
            throw new IllegalArgumentException(String.format("Given codec '%s' is not a video codec!", codec.name()));

        var supportedPixFormatNames = new HashSet<String>();

        var pix_fmts = codec.pix_fmts().getStringCodePoints();

        // TODO does this work?
        for (var pixFormat : pix_fmts) {
            supportedPixFormatNames.add(avutil.av_get_pix_fmt_name(pixFormat).getString());
        }

        return supportedPixFormatNames;
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
        return iterate(avcodec::av_codec_iterate);
    }
}
