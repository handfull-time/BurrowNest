package com.utime.burrowNest.storage.vo;

import java.sql.Date;
import java.sql.Time;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents an image file with metadata such as camera details, dimensions,
 * GPS coordinates, and other photographic properties.
 * This class extends the {@link AbsBnFileInfo} abstract class.
 */
@Setter
@Getter
@ToString(callSuper = true)
public class BnFileImage extends AbsBnFileInfo {
	/**
	 * Represents the make of the camera.
	 * 카메라 제조사를 나타냅니다.
	 */
	private String make;

	/**
	 * Represents the model name of the camera.
	 * 카메라 모델명을 나타냅니다.
	 */
	private String cameraModelName;

	/**
	 * Represents the orientation of the image.
	 * 이미지의 방향(회전 상태)을 나타냅니다.
	 */
	private String orientation;

	/**
	 * Represents the f-number (aperture setting).
	 * F값(조리개 설정)을 나타냅니다.
	 */
	private double fNumber;

	/**
	 * Represents the exposure time in seconds.
	 * 노출 시간을 초 단위로 나타냅니다.
	 */
	private String exposureTime;

	/**
	 * Represents the sensing method used by the camera sensor.
	 * 카메라 센서의 감지 방식을 나타냅니다.
	 */
	private String sensingMethod;

	/**
	 * Represents whether the flash was used.
	 * 플래시 사용 여부를 나타냅니다.
	 */
	private String flash;

	/**
	 * Represents the light source used in the image capture.
	 * 이미지 촬영에 사용된 광원을 나타냅니다.
	 */
	private String lightSource;

	/**
	 * Represents the white balance setting.
	 * 화이트 밸런스 설정을 나타냅니다.
	 */
	private String whiteBalance;

	/**
	 * Represents the calculated shutter speed.
	 * 계산된 셔터 속도를 나타냅니다.
	 */
	private double shutterSpeed;

	/**
	 * Represents the ISO sensitivity setting.
	 * ISO 감도 설정을 나타냅니다.
	 */
	private int iso;

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
	 * Represents the creation date of the image.
	 * 이미지의 생성 날짜를 나타냅니다.
	 */
	private String createDate;

	/**
	 * Represents the original date and time of the image capture.
	 * 이미지 원본 촬영 날짜와 시간을 나타냅니다.
	 */
	private String dateTimeOriginal;

	/**
	 * Represents the last modification date of the image.
	 * 이미지의 마지막 수정 날짜를 나타냅니다.
	 */
	private String modifyDate;

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
}