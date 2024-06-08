package helper;

import eu.kaesebrot.dev.transcodeservice.models.AudioTrackPreset;
import eu.kaesebrot.dev.transcodeservice.models.VideoTrackPreset;
import eu.kaesebrot.dev.transcodeservice.models.rest.TranscodePresetCreation;

import java.util.Set;

public final class DataHelper {
    private DataHelper() {}

    public static TranscodePresetCreation getCreationPreset() {
        VideoTrackPreset videoTrackPreset = getVideoTrackPreset();
        AudioTrackPreset audioTrackPreset = getAudioTrackPreset();

        // the preset creation model itself
        String description = "h264 testing preset";
        String muxer = "mp4";

        TranscodePresetCreation creationPreset = new TranscodePresetCreation();

        creationPreset.setDescription(description);
        creationPreset.setMuxer(muxer);
        creationPreset.setTrackPresets(Set.of(videoTrackPreset, audioTrackPreset));

        return creationPreset;
    }

    public static VideoTrackPreset getVideoTrackPreset() {
        // video track preset
        Double framerate = 25.0;
        String videoBitrate = "10m";
        Integer width = 1920;
        Integer height = 1080;
        String videoCodec = "libx264";
        String pixelFormat = "yuv420p";

        VideoTrackPreset videoTrackPreset = new VideoTrackPreset();
        videoTrackPreset.setFramerate(framerate);
        videoTrackPreset.setVideoBitrate(videoBitrate);
        videoTrackPreset.setWidth(width);
        videoTrackPreset.setHeight(height);
        videoTrackPreset.setVideoCodecName(videoCodec);
        videoTrackPreset.setVideoPixelFormat(pixelFormat);

        return videoTrackPreset;
    }

    public static AudioTrackPreset getAudioTrackPreset() {
        // audio track preset
        String audioBitrate = "192k";
        String audioCodec = "aac";
        Integer audioSampleRate = 44100;

        AudioTrackPreset audioTrackPreset = new AudioTrackPreset();
        audioTrackPreset.setAudioBitrate(audioBitrate);
        audioTrackPreset.setAudioCodecName(audioCodec);
        audioTrackPreset.setAudioSampleRate(audioSampleRate);

        return audioTrackPreset;
    }
}
