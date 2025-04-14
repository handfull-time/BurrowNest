package com.utime.burrowNest.storage.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents metadata and analysis results for an audio file stored in BN_FILE_AUDIO table.
 */
@Setter
@Getter
@ToString( callSuper = true)
public class BnFileAudio extends AbsBnFileInfor{

    /**
     * Total playback duration (in seconds or minutes).
     */
    private int duration;

    /**
     * Audio format of the file (e.g., MP3, WAV, FLAC).
     */
    private String audioFormat;

    /**
     * Sampling rate in Hz (e.g., 44100).
     */
    private int sampleRate;

    /**
     * Channel configuration (e.g., Mono, Stereo, Surround).
     */
    private String channels;

    /**
     * Bit depth representing sound quality (e.g., 16, 24).
     */
    private int bitDepth;

    /**
     * Audio codec used for compression (e.g., AAC, MP3).
     */
    private String audioCodec;

    /**
     * Bitrate of the audio in kbps (e.g., 320).
     */
    private int bitrate;

    /**
     * Minimum volume level detected in the track.
     */
    private float volumeLevelMin;

    /**
     * Maximum volume level detected in the track.
     */
    private float volumeLevelMax;

    /**
     * Average volume level across the track.
     */
    private float volumeLevelAvg;

    /**
     * Frequency spectrum information (e.g., bass, mid, treble).
     */
    private String frequencySpectrum;

    /**
     * Year the song or album was released.
     */
    private int releaseYear;

    /**
     * Title of the song.
     */
    private String songTitle;

    /**
     * Title of the album the song belongs to.
     */
    private String albumTitle;

    /**
     * Name of the artist who created the track.
     */
    private String artistName;

    /**
     * Embedded lyrics within the audio file, if available.
     */
    private String embeddedLyrics;
}
