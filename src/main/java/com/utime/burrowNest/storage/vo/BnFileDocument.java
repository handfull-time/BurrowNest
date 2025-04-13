package com.utime.burrowNest.storage.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a video file with metadata such as duration, resolution, frame rate,
 * and codec information. This class extends the {@link AbsBnFileInfor} abstract class.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class BnFileDocument extends AbsBnFileInfor {

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
     * Category or classification of the document.
     */
    private String category;

    /**
     * Keywords associated with the document, for easier searching or filtering.
     */
    private String keywords;

    /**
     * Additional notes or comments about the document.
     */
    private String notes;
}