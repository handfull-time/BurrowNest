package com.utime.burrowNest.storage.vo;

import java.sql.Timestamp;

/**
 * Abstract class representing common file information shared across different file types.
 * This class serves as a base for specific file type implementations such as Document, Image, Video, Audio, and Archive.
 */
public abstract class AbsBnFileInfor {
    /**
     * Unique identifier for the file.
     */
    protected long fileNo;

    /**
     * The date and time when the file was last updated or modified.
     */
    protected Timestamp updateDate;
}