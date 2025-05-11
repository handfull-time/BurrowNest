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
    protected boolean publicAccessible;

    /**
     * Indicates whether the directory has child directories.
     * True for having children, false otherwise.
     */
    protected boolean hasChild;

    /**
     * The absolute path of the directory in the file system.
     */
    protected String absolutePath;
}