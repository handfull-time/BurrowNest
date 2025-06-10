package com.utime.burrowNest.storage.vo;

import java.time.LocalDateTime;

/**
 * Abstract class representing common file information shared across different file types.
 * This class serves as a base for specific file type implementations such as Document, Image, Video, Audio, and Archive.
 */
public abstract class AbsBnFileInfo {
    /**
     * Unique identifier for the file.
     */
    protected long fileNo = -1L;

    /**
     * The date and time when the file was last updated or modified.
     */
    protected LocalDateTime updateDate;

	public long getFileNo() {
		return fileNo;
	}

	public void setFileNo(long fileNo) {
		this.fileNo = fileNo;
	}

	public LocalDateTime getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(LocalDateTime updateDate) {
		this.updateDate = updateDate;
	}
    
    
}