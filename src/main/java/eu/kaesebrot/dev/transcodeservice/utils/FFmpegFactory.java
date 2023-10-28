package eu.kaesebrot.dev.transcodeservice.utils;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffprobe.FFprobe;

import java.nio.file.Path;

public final class FFmpegFactory {
    private FFmpegFactory() {}

    private static Path fFprobePath = null;
    private static Path fFmpegPath = null;

    public static void setFFprobePath(Path path) {
        if (!path.isAbsolute())
            throw new IllegalArgumentException("Path has to be absolute!");

        if (!path.toFile().isFile())
            throw new IllegalArgumentException("Path is not a file!");

        FFmpegFactory.fFprobePath = path;
    }

    public static void setFFmpegPath(Path path) {
        if (!path.isAbsolute())
            throw new IllegalArgumentException("Path has to be absolute!");

        if (!path.toFile().isFile())
            throw new IllegalArgumentException("Path is not a file!");

        FFmpegFactory.fFmpegPath = path;
    }

    public static FFmpeg getFFmpeg() {
        if (fFmpegPath != null)
            return FFmpeg.atPath(fFmpegPath);

        return FFmpeg.atPath();
    }

    public static FFprobe getFFprobe() {
        if (fFprobePath != null)
            return FFprobe.atPath(fFprobePath);

        return FFprobe.atPath();
    }
}
