package com.utime.burrowNest.storage.vo;

import java.time.LocalDateTime;

import com.utime.burrowNest.common.util.BurrowUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a video file with metadata such as duration, resolution, frame rate,
 * and codec information. This class extends the {@link AbsBnFileInfo} abstract class.
 */
@Getter
@Setter
public class BnFileVideo extends AbsBnFileInfo {
	/**
	 * Represents the MIME type of the file.
	 * 파일의 MIME 타입을 나타냅니다.
	 */
	private String mimeType;

	/**
	 * Represents the author of the file.
	 * 파일의 작성자를 나타냅니다.
	 */
	private String author;

	/**
	 * Represents the duration of the media file.
	 * 미디어 파일의 재생 시간을 나타냅니다.
	 */
	private String duration;

	/**
	 * Represents the preferred playback rate.
	 * 선호하는 재생 속도를 나타냅니다.
	 */
	private double preferredRate;

	/**
	 * Represents the preferred playback volume.
	 * 선호하는 재생 볼륨을 나타냅니다.
	 */
	private String preferredVolume;

	/**
	 * Represents the format of the audio file.
	 * 오디오 파일의 형식을 나타냅니다.
	 */
	private String audioFormat;

	/**
	 * Represents the number of bits per audio sample.
	 * 오디오 샘플당 비트 수를 나타냅니다.
	 */
	private int audioBitsPerSample;

	/**
	 * Represents the audio sample rate in Hz.
	 * 오디오 샘플링 속도를 Hz 단위로 나타냅니다.
	 */
	private int audioSampleRate;

	/**
	 * Represents the layout flags used in the media file.
	 * 미디어 파일에서 사용되는 레이아웃 플래그를 나타냅니다.
	 */
	private String layoutFlags;

	/**
	 * Represents the number of audio channels.
	 * 오디오 채널 수를 나타냅니다.
	 */
	private int audioChannels;

	/**
	 * Represents the version of the compressor used.
	 * 사용된 압축기 버전을 나타냅니다.
	 */
	private String compressorVersion;

	/**
	 * Represents the camera model name.
	 * 카메라 모델명을 나타냅니다.
	 */
	private String cameraModelName;

	/**
	 * Represents the firmware version of the camera.
	 * 카메라의 펌웨어 버전을 나타냅니다.
	 */
	private String firmwareVersion;

	/**
	 * Represents the average bitrate of the media file.
	 * 미디어 파일의 평균 비트레이트를 나타냅니다.
	 */
	private String avgBitrate;

	/**
	 * Represents the rotation angle of the image or video.
	 * 이미지 또는 비디오의 회전 각도를 나타냅니다.
	 */
	private int rotation;

	/**
	 * Represents the width of the image in pixels.
	 * 이미지의 너비(픽셀 단위)를 나타냅니다.
	 */
	private int imageWidth;

	/**
	 * Represents the height of the image in pixels.
	 * 이미지의 높이(픽셀 단위)를 나타냅니다.
	 */
	private int imageHeight;

	/**
	 * Represents the creation date of the media file.
	 * 미디어 파일의 생성 날짜를 나타냅니다.
	 */
	private LocalDateTime createDate;

	/**
	 * Represents the last modification date of the media file.
	 * 미디어 파일의 마지막 수정 날짜를 나타냅니다.
	 */
	private LocalDateTime modifyDate;

	/**
	 * Represents the latitude coordinate of the image location.
	 * 이미지가 촬영된 위치의 위도를 나타냅니다.
	 */
	private double gpsLatitude;

	/**
	 * Represents the longitude coordinate of the image location.
	 * 이미지가 촬영된 위치의 경도를 나타냅니다.
	 */
	private double gpsLongitude;
	
	@Override
	public String toString() {
		return BurrowUtils.toJson(this);
	}
}