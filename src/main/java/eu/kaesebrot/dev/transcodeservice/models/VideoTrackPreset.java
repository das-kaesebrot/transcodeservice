package eu.kaesebrot.dev.transcodeservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.kaesebrot.dev.transcodeservice.constants.ETrackPresetType;
import eu.kaesebrot.dev.transcodeservice.utils.AVUtils;
import eu.kaesebrot.dev.transcodeservice.utils.StringUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.lang.Nullable;

import java.util.Map;

public class VideoTrackPreset extends TrackPreset {
    @NotBlank
    @JsonProperty("video_codec")
    private String videoCodecName;

    // nullable for carrying over from input
    @Nullable
    @Positive
    private Double framerate;
    // nullable for carrying over from input
    @Nullable
    @Positive
    private Integer width;
    // nullable for carrying over from input
    @Nullable
    @Positive
    private Integer height;
    @Nullable
    @JsonProperty("video_bitrate")
    private String videoBitrate;
    @Nullable
    @JsonProperty("video_pixel_format")
    private String videoPixelFormat;
    @JsonProperty("video_options")
    private Map<String, String> videoOptions;

    public String getVideoCodecName() {
        return videoCodecName;
    }

    public void setVideoCodecName(String videoCodecName) {
        if (StringUtils.isNullOrEmpty(videoCodecName)) {
            this.videoCodecName = "copy"; // TODO check this
            return;
        }

        if (!AVUtils.getSupportedVideoEncoders().contains(videoCodecName)) {
            throw new IllegalArgumentException(String.format("Given codec '%s' is not supported!", videoCodecName));
        }

        this.videoCodecName = videoCodecName;
    }

    @Nullable
    public Double getFramerate() {
        return framerate;
    }

    public void setFramerate(@Nullable Double framerate) {
        this.framerate = framerate;
    }

    @Nullable
    public Integer getWidth() {
        return width;
    }

    public void setWidth(@Nullable Integer width) {
        this.width = width;
    }

    @Nullable
    public Integer getHeight() {
        return height;
    }

    public void setHeight(@Nullable Integer height) {
        this.height = height;
    }

    @Nullable
    public String getVideoBitrate() {
        return videoBitrate;
    }

    public void setVideoBitrate(@Nullable String videoBitrate) {
        this.videoBitrate = videoBitrate;
    }

    @Nullable
    public String getVideoPixelFormat() {
        return videoPixelFormat;
    }

    public void setVideoPixelFormat(@Nullable String videoPixelFormat) {
        if (videoCodecName == null) {
            // TODO maybe throw later in case this preset only copies
            return;
        }

        if (!AVUtils.getSupportedPixelFormatNamesForCodec(videoCodecName).isEmpty() && !AVUtils.getSupportedPixelFormatNamesForCodec(videoCodecName).contains(videoPixelFormat)) {
            throw new IllegalArgumentException(String.format("Given pixel format '%s' is not supported by codec '%s'!", videoPixelFormat, videoCodecName));
        }

        this.videoPixelFormat = videoPixelFormat;
    }

    public Map<String, String> getVideoOptions() {
        return videoOptions;
    }

    public void setVideoOptions(Map<String, String> videoOptions) {
        this.videoOptions = videoOptions;
    }

    public ETrackPresetType getType() {
        return ETrackPresetType.VIDEO;
    }
}
