package com.utime.burrowNest.storage.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a directory entity with various properties such as name, creation date,
 * modification date, and hierarchical structure information.
 */
@Setter
@Getter
@ToString( callSuper = true )
public class BnDirectory extends AbsPath{
    
    /**
     * Indicates whether this directory is publicly accessible by all users.
     */
    private boolean publicAccessible;

    /**
     * Indicates whether the directory has child directories.
     * True for having children, false otherwise.
     */
    private boolean hasChild;

    /**
     * The pure name of the directory.
     */
    private String name;

    /**
     * The absolute path of the directory in the file system.
     */
    private String absolutePath;
}