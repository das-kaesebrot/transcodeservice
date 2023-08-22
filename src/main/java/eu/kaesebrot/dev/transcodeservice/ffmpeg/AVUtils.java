package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avformat.AVInputFormat;
import org.bytedeco.ffmpeg.avformat.AVOutputFormat;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avformat;
import org.bytedeco.javacpp.Pointer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public final class AVUtils {
    private AVUtils() {}

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

    /**
     * See: https://ffmpeg.org/pipermail/libav-user/2018-May/011160.html
     * @return
     */
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
