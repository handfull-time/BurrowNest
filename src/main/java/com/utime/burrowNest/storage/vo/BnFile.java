package com.utime.burrowNest.storage.vo;

import com.utime.burrowNest.common.util.BurrowUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a basic file entity in the system. This class encapsulates common properties
 * of a file such as its ID, creation date, modification date, type, and related metadata.
 * It serves as the main data structure for handling files.
 */
@Setter
@Getter
public class BnFile extends AbsPath {
 
    /**
     * The size of the file in bytes.
     */
    protected long fileLength;

    /**
     * The type of the file, as defined in the {@link EBnFileType} enumeration.
     */
    protected EBnFileType fileType;

    /**
     * The full name of the file, including its extension.
     * For example, "document.txt" or "photo.jpeg".
     */
    protected String fullName;

    /**
     * The file extension (e.g., ".txt", ".jpg").
     */
    protected String extension;

    /**
     * extends file information
     */
    protected AbsBnFileInfo info;
    
    @Override
	public String toString() {
		return BurrowUtils.toJson(this);
	}

}