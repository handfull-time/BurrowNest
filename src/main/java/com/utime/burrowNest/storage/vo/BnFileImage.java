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
     * The model name of the camera used to capture the image.
     */
    private String cameraModel;

    /**
     * The manufacturer of the camera.
     */
    private String cameraManufacturer;

    /**
     * The aperture value used during image capture.
     */
    private float apertureValue;

    /**
     * The shutter speed used during image capture.
     */
    private float shutterSpeed;

    /**
     * The ISO sensitivity value of the camera.
     */
    private int isoValue;

    /**
     * The focal length of the lens used for image capture.
     */
    private float focalLength;

    /**
     * The date the image was captured.
     */
    private Date captureDate;

    /**
     * The time the image was captured.
     */
    private Time captureTime;

    /**
     * The width of the image in pixels.
     */
    private int imageWidth;

    /**
     * The height of the image in pixels.
     */
    private int imageHeight;

    /**
     * The GPS latitude where the image was captured.
     */
    private double gpsLatitude;

    /**
     * The GPS longitude where the image was captured.
     */
    private double gpsLongitude;

    /**
     * Indicates whether the camera flash was used during capture.
     */
    private boolean flashUsed;

    /**
     * The white balance setting used during image capture.
     */
    private String whiteBalance;

    /**
     * The scene mode used during image capture.
     */
    private String sceneMode;

    /**
     * The version of the software used for image processing.
     */
    private String softwareVersion;
}