package eu.kaesebrot.dev.transcodeservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

// TODO
@Entity
@Table(name = "transcode_preset")
public class TranscodePreset implements Serializable {

    @javax.persistence.Version
    @Column(name = "version")
    @JsonProperty("version")
    private long version;

    @javax.persistence.Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    @JsonProperty("id")
    private Long id;

    private String description;
    @JsonProperty("video_codec")
    private String videoCodecName;
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

    @JsonProperty("container")
    private String muxer;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @JsonProperty("created_at")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    @JsonProperty("modified_at")
    private Timestamp modifiedAt;

    @OneToMany(mappedBy = "preset")
    private Set<TranscodeJob> jobs;

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getModifiedAt() {
        return modifiedAt;
    }

    public long getVersion() {
        return version;
    }

    public Long getId() {
        return id;
    }

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

    public Set<TranscodeJob> getJobs() {
        return jobs;
    }

    public void setJobs(Set<TranscodeJob> jobs) {
        this.jobs = jobs;
    }

    /*
    * TODO
    # for usage with x264/x265
    profile = Column(String)
    tune = Column(String)
    crf = Column(Integer)

    videofilter = Column(String)
    audiofilter = Column(String)
    */

}
