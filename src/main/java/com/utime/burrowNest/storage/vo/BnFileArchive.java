package com.utime.burrowNest.storage.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents archive metadata for compressed files in the BN_FILE_ARCHIVE table.
 * This class extends the {@link AbsBnFileInfo} abstract class.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class BnFileArchive extends AbsBnFileInfo {
	/**
     * The format of the archive (ZIP, RAR, 7Z, etc).
     */
    private EArchiveType archiveFormat;

    /**
     * The number of entries (files/folders) in the archive.
     */
    private int totalEntries;

    /**
     * The total uncompressed size in bytes.
     */
    private long uncompressedSize;

    /**
     * The compression ratio. (e.g., 0.75 = 75%)
     */
    private float compressionRatio;

    /**
     * Whether the archive is encrypted.
     */
    private boolean encrypted;

    /**
     * Whether the archive is protected by password.
     */
    private boolean passwordProtected;

    /**
     * Optional comment string embedded in the archive.
     */
    private String comment;    
}

