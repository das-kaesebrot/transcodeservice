package eu.kaesebrot.dev.transcodeservice.ffmpeg;

import com.github.manevolent.ffmpeg4j.FFmpegException;
import org.bytedeco.ffmpeg.avformat.AVOutputFormat;

import java.util.List;

public interface FfmpegUtilities {
    List<String> getSupportedVideoEncoders();
    List<String> getSupportedAudioEncoders();
    List<String> getSupportedVideoDecoders();
    List<String> getSupportedAudioDecoders();
    List<AVOutputFormat> getSupportedMuxers();
    boolean muxerSupportsCodec(AVOutputFormat muxer, String codecName) throws FFmpegException;
}
