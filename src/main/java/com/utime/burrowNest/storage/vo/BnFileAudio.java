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
public class BnFileAudio extends AbsBnFileInfo{
	
	/**
	 * Represents the MIME type of the file.
	 * 파일의 MIME 타입을 나타냅니다.
	 */
	private String mimeType;

	/**
     * Sample rate of the audio file
     * 오디오 파일의 샘플 레이트
     */
    private int sampleRate;

    /**
     * Number of channels in the audio file
     * 오디오 파일의 채널 수
     */
    private int channels;

    /**
     * Bits per sample of the audio file
     * 오디오 파일의 샘플 당 비트 수
     */
    private int bitsPerSample;

    /**
     * Total samples in the audio file
     * 오디오 파일의 총 샘플 수
     */
    private long totalSamples;

    /**
     * Title of the track
     * 트랙 제목
     */
    private String title;

    /**
     * Name of the artist
     * 아티스트 이름
     */
    private String artist;

    /**
     * Album name
     * 앨범 이름
     */
    private String album;

    /**
     * Genre of the track
     * 트랙 장르
     */
    private String genre;

    /**
     * Album artist name
     * 앨범 아티스트 이름
     */
    private String albumArtist;

    /**
     * Disc number of the album
     * 앨범 디스크 번호
     */
    private int discNumber;

    /**
     * Release date of the track
     * 트랙 발매 날짜
     */
    private String date;

    /**
     * Organization associated with the track
     * 트랙과 관련된 조직
     */
    private String organization;

    /**
     * Track number in the album
     * 앨범 내 트랙 번호
     */
    private int trackNumber;

    /**
     * Duration of the track in seconds
     * 트랙의 재생 길이(초)
     */
    private String duration;
}
