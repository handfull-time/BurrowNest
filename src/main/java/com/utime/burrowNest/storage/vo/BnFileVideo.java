package com.utime.burrowNest.storage.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a video file with metadata such as duration, resolution, frame rate,
 * and codec information. This class extends the {@link AbsBnFileInfo} abstract class.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class BnFileVideo extends AbsBnFileInfo {
    /**
     * The total playback duration of the video in seconds.
     */
    private int duration;

    /**
     * The resolution of the video (e.g., "1920x1080").
     */
    private String resolution;

    /**
     * The frame rate of the video in frames per second (FPS).
     */
    private float frameRate;

    /**
     * The video codec used to encode the video (e.g., "H.264", "H.265").
     */
    private String videoCodec;

    /**
     * The audio codec used in the video file (e.g., "AAC", "MP3").
     */
    private String audioCodec;

    /**
     * The bitrate of the video and audio streams in kilobits per second (kbps).
     */
    private int bitrate;

    /**
     * Metadata tags associated with the video file.
     */
    private String tags;
}