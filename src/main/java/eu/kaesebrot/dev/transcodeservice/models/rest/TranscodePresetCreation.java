package eu.kaesebrot.dev.transcodeservice.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class TranscodePresetCreation implements Serializable {
    private String description;
    @NotNull
    @JsonProperty("video_codec")
    private String videoCodecName;
    @NotNull
    @JsonProperty("audio_codec")
    private String audioCodecName;

    // nullable for carrying over from input
    @Nullable
    private Double framerate;
    // nullable for carrying over from input
    @Nullable
    private Long width;
    // nullable for carrying over from input
    @Nullable
    private Long height;
    @Nullable
    @JsonProperty("video_bitrate")
    private String videoBitrate;
    @Nullable
    @JsonProperty("audio_bitrate")
    private String audioBitrate;
    @Nullable
    @JsonProperty("audio_sample_rate")
    private String audioSampleRate;

    @Nullable
    @JsonProperty("video_picture_format")
    private String videoPictureFormat;
    @NotNull
    @JsonProperty("container")
    private String muxer;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoCodecName() {
        return videoCodecName;
    }

    public void setVideoCodecName(String videoCodecName) {
        this.videoCodecName = videoCodecName;
    }

    public String getAudioCodecName() {
        return audioCodecName;
    }

    public void setAudioCodecName(String audioCodecName) {
        this.audioCodecName = audioCodecName;
    }

    @Nullable
    public Double getFramerate() {
        return framerate;
    }

    public void setFramerate(@Nullable Double framerate) {
        this.framerate = framerate;
    }

    @Nullable
    public Long getWidth() {
        return width;
    }

    public void setWidth(@Nullable Long width) {
        this.width = width;
    }

    @Nullable
    public Long getHeight() {
        return height;
    }

    public void setHeight(@Nullable Long height) {
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
    public String getAudioBitrate() {
        return audioBitrate;
    }

    public void setAudioBitrate(@Nullable String audioBitrate) {
        this.audioBitrate = audioBitrate;
    }

    @Nullable
    public String getAudioSampleRate() {
        return audioSampleRate;
    }

    public void setAudioSampleRate(@Nullable String audioSampleRate) {
        this.audioSampleRate = audioSampleRate;
    }

    @Nullable
    public String getVideoPictureFormat() {
        return videoPictureFormat;
    }

    public void setVideoPictureFormat(@Nullable String videoPictureFormat) {
        this.videoPictureFormat = videoPictureFormat;
    }

    public String getMuxer() {
        return muxer;
    }

    public void setMuxer(String muxer) {
        this.muxer = muxer;
    }
}
