package com.utime.burrowNest.storage.vo;

import java.sql.Timestamp;

import lombok.Data;

/**
 * Represents a basic file entity in the system. This class encapsulates common properties
 * of a file such as its ID, creation date, modification date, type, and related metadata.
 * It serves as the main data structure for handling files.
 */
@Data
public class BnFile {
    /**
     * Unique identifier for the file.
     */
    private long no;

    /**
     * The date and time when the file was registered or created.
     */
    private Timestamp regDate;

    /**
     * The date and time when the file was last updated or modified.
     */
    private Timestamp updateDate;

    /**
     * Indicates whether the file is enabled or active.
     */
    private boolean enabled;

    /**
     * The unique identifier of the parent directory that contains the file.
     */
    private long parentNo;

    /**
     * The size of the file in bytes.
     */
    private long fileLength;

    /**
     * The type of the file, as defined in the {@link EBnFileType} enumeration.
     */
    private EBnFileType fileType;

    /**
     * The date and time when the folder or file was created.
     */
    private Timestamp creation;

    /**
     * The date and time when the folder or file was last modified.
     */
    private Timestamp lastModified;

    /**
     * The file extension (e.g., ".txt", ".jpg").
     */
    private String extension;

    /**
     * The name of the file excluding its extension.
     */
    private String name;
    
    /**
     * extends file information
     */
    private AbsBnFileInfor infor;

}