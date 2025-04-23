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
public class BnFileDocument extends AbsBnFileInfo {

    /**
     * Title of the document.
     */
    private String title;

    /**
     * Subject or topic of the document.
     */
    private String subject;

    /**
     * Author or creator of the document.
     */
    private String author;

    /**
     * Manager or administrator responsible for the document.
     */
    private String manager;

    /**
     * Company associated with the document.
     */
    private String company;

    /**
     * 작성한 어플리케이션
     */
    private String application;

    /**
     * Keywords associated with the document, for easier searching or filtering.
     */
    private String keywords;

    /**
     * Additional notes or comments about the document.
     */
    private String notes;
}