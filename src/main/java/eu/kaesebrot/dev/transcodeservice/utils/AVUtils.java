package eu.kaesebrot.dev.transcodeservice.utils;

import eu.kaesebrot.dev.transcodeservice.constants.EEncoderType;
import eu.kaesebrot.dev.transcodeservice.models.core.Encoder;
import eu.kaesebrot.dev.transcodeservice.models.core.Muxer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AVUtils {
    private static List<String> videoEncoderNames = new ArrayList<>();
    private static List<String> audioEncoderNames = new ArrayList<>();
    private static final List<Muxer> muxers = new ArrayList<>();
    private static final List<Encoder> encoders = new ArrayList<>();
    private static Map<String, List<Integer>> supportedAudioSampleRatesPerCodec = new HashMap<>();
    private static Map<String, List<String>> supportedVideoPixFmtsPerCodec = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger("AVUtils");

    public static List<Integer> getSupportedAudioSampleRatesForCodec(String codecName) {
        return getSupportedAudioSampleRates().getOrDefault(codecName, List.of());
    }

    public static List<String> getSupportedPixelFormatNamesForCodec(String codecName) {
            return getSupportedPixelFormatNames().getOrDefault(codecName, List.of());
    }

    public static Map<String, List<Integer>> getSupportedAudioSampleRates() {
        if (supportedAudioSampleRatesPerCodec != null && !supportedAudioSampleRatesPerCodec.isEmpty())
            return supportedAudioSampleRatesPerCodec;

        Map<String, List<Integer>> resultMap = new TreeMap<>();

        for (var encoder : getEncoders()) {
            if (encoder.type() != EEncoderType.AUDIO)
                continue;

            final String search_str = "Supported sample rates";

            String sample_rates_str = null;
            for (var line : runFFmpegProcess(List.of("-loglevel", "level+quiet", "-n", "-h", String.format("encoder=%s", encoder.name())))) {
                if (line.contains(search_str)) {
                    sample_rates_str = line;
                    break;
                }
            }

            if (sample_rates_str == null)
                continue;

            List<Integer> sample_rates_arr = Arrays.stream(sample_rates_str
                            .strip()
                            .toLowerCase()
                            .substring(search_str.length() + 1)
                            .split(" "))
                            .filter(item-> !item.isEmpty())
                            .map(Integer::parseInt)
                            .toList();

            resultMap.put(encoder.name(), sample_rates_arr);
        }

        supportedAudioSampleRatesPerCodec = resultMap;

        return supportedAudioSampleRatesPerCodec;
    }

    public static Map<String, List<String>> getSupportedPixelFormatNames() {
        if (supportedVideoPixFmtsPerCodec != null && !supportedVideoPixFmtsPerCodec.isEmpty())
            return supportedVideoPixFmtsPerCodec;

        Map<String, List<String>> resultMap = new TreeMap<>();

        for (var encoder : getEncoders()) {
            if (encoder.type() != EEncoderType.VIDEO)
                continue;

            final String search_str = "Supported pixel formats";

            String pix_fmt_string = null;
            for (var line : runFFmpegProcess(List.of("-loglevel", "level+quiet", "-n", "-h", String.format("encoder=%s", encoder.name())))) {
                if (line.contains(search_str)) {
                    pix_fmt_string = line;
                    break;
                }
            }

            if (pix_fmt_string == null)
                continue;

            List<String> pix_fmts = Arrays.stream(pix_fmt_string
                            .strip()
                            .toLowerCase()
                            .substring(search_str.length() + 1)
                            .split(" "))
                            .filter(item-> !item.isEmpty())
                            .toList();

            resultMap.put(encoder.name(), pix_fmts);
        }

        supportedVideoPixFmtsPerCodec = resultMap;

        return supportedVideoPixFmtsPerCodec;
    }

    public static List<String> getSupportedVideoEncoders() {
        if (videoEncoderNames == null || videoEncoderNames.isEmpty()) {
            videoEncoderNames = getEncoders().stream()
                    .filter(c -> c.type() == EEncoderType.VIDEO)
                    .map(Encoder::name)
                    .toList();
        }

        return videoEncoderNames;
    }

    public static List<String> getSupportedAudioEncoders() {
        if (audioEncoderNames == null || audioEncoderNames.isEmpty()) {
            audioEncoderNames = getEncoders().stream()
                    .filter(c -> c.type() == EEncoderType.AUDIO)
                    .map(Encoder::name)
                    .toList();
        }

        return audioEncoderNames;
    }

    public static List<String> getSupportedMuxerNames() {
        return getMuxers().stream().map(Muxer::name).toList();
    }

    public static Collection<Muxer> getMuxers() {
        if (!muxers.isEmpty())
            return muxers;

        int skipBeginning = 4;

        for (String line : runFFmpegProcess(List.of("-loglevel", "level+quiet", "-n", "-muxers"))) {
            if (0 < skipBeginning--) continue;

            line = line.strip();
            var rows = line.split(" ");

            if (rows.length < 2)
                continue;

            String name = rows[1];
            String long_name = line.substring(2 + name.length()).strip();

            muxers.add(new Muxer(name, long_name));
        }

        return muxers;
    }

    public static Collection<Encoder> getEncoders() {
        if (!encoders.isEmpty())
        {
            return encoders;
        }

        int skipBeginning = 9;

        for (String line : runFFmpegProcess(List.of("-loglevel", "level+quiet", "-n", "-encoders"))) {
            if (0 < skipBeginning--) continue;

            line = line.strip();
            var rows = line.split(" ");

            if (rows.length < 2)
                continue;

            String flags = rows[0].toLowerCase();
            String name = rows[1];
            String long_name = line.substring(flags.length() + name.length()).strip();

            // Flags evaluation
            /*
             V..... = Video
             A..... = Audio
             S..... = Subtitle
             .F.... = Frame-level multithreading
             ..S... = Slice-level multithreading
             ...X.. = Codec is experimental
             ....B. = Supports draw_horiz_band
             .....D = Supports direct rendering method 1
            */
            // only evaluate type for now
            EEncoderType type = switch (flags.charAt(0)) {
                case 'v' -> EEncoderType.VIDEO;
                case 'a' -> EEncoderType.AUDIO;
                case 's' -> EEncoderType.SUBTITLE;
                default -> throw new RuntimeException("Unknown encoder flag: " + flags.charAt(0));
            };

            encoders.add(new Encoder(name, long_name, type));
        }

        return encoders;
    }

    private static List<String> runFFmpegProcess(List<String> args) {
        List<String> lines = new ArrayList<>();

        // add ffmpeg binary as the first arg
        ArrayList<String> cmd = new ArrayList<>(args);
        cmd.add(0, "ffmpeg");

        try {
            Process process = new ProcessBuilder(cmd).start();

            try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;

                while ((line = input.readLine()) != null) {
                    lines.add(line);
                }
            }

            process.waitFor(10, TimeUnit.MILLISECONDS);
        } catch (IOException | InterruptedException e) {
            logger.error("Exception while running ffmpeg: ", e);
            throw new RuntimeException(e);
        }

        return lines;
    }
}
