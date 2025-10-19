package com.utime.burrowNest.storage.vo;

import com.utime.burrowNest.common.util.BurrowUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a directory entity with various properties such as name, creation date,
 * modification date, and hierarchical structure information.
 */
@Setter
@Getter
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
     * 반드시 끝 문자열은 '/' 로 끝날 것.
     */
    protected String absolutePath;
    
    @Override
	public String toString() {
		return BurrowUtils.toJson(this);
	}
}