package eu.kaesebrot.dev.transcodeservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.kaesebrot.dev.transcodeservice.constants.ETrackPresetType;
import org.springframework.lang.Nullable;

public class VideoTrackPreset extends TrackPreset {
    @JsonProperty("video_codec")
    private String videoCodecName;

    // nullable for carrying over from input
    @Nullable
    private Double framerate;
    // nullable for carrying over from input
    @Nullable
    private Integer width;
    // nullable for carrying over from input
    @Nullable
    private Integer height;
    @Nullable
    @JsonProperty("video_bitrate")
    private String videoBitrate;

    @Nullable
    @JsonProperty("video_pixel_format")
    private String videoPixelFormat;

    public String getVideoCodecName() {
        return videoCodecName;
    }

    public void setVideoCodecName(String videoCodecName) {
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
        this.videoPixelFormat = videoPixelFormat;
    }
    public ETrackPresetType getType() {
        return ETrackPresetType.VIDEO;
    }
}
