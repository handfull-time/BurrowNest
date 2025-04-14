package com.utime.burrowNest.storage.vo;

import lombok.Data;
import java.sql.Timestamp;

/**
 * Represents a directory entity with various properties such as name, creation date,
 * modification date, and hierarchical structure information.
 */
@Data
public class BnDirectory {

    /**
     * The primary key of the directory entity.
     */
    private long no;

    /**
     * The registration date of the directory.
     */
    private Timestamp regDate;

    /**
     * The last update date of the directory.
     */
    private Timestamp updateDate;

    /**
     * Indicates whether the directory is enabled or not. 
     * True for enabled, false for disabled.
     */
    private boolean enabled;

    /**
     * The parent directory's primary key. Represents the parent-child relationship.
     * root is -1;
     */
    private long parentNo;

    /**
     * Indicates whether the directory has child directories.
     * True for having children, false otherwise.
     */
    private boolean hasChild;

    /**
     * The creation timestamp of the folder.
     */
    private Timestamp creation;

    /**
     * The last modified timestamp of the folder.
     */
    private Timestamp lastModified;

    /**
     * The pure name of the directory.
     */
    private String name;

    /**
     * The absolute path of the directory in the file system.
     */
    private String absolutePath;
}