package com.utime.burrowNest.storage.vo;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class AbsPath {

    /**
     * The primary key of the directory(file) entity.
     */
    protected long no = -1L;

    /**
     * The registration date of the directory(file).
     */
    protected Timestamp regDate;

    /**
     * The last update date of the directory(file).
     */
    protected Timestamp updateDate;

    /**
     * Indicates whether the directory(file) is enabled or not. 
     * True for enabled, false for disabled.
     */
    protected boolean enabled;
    
    /**
     * unique id
     */
    protected String uid;
    
    /**
     * The unique identifier of the parent directory that contains the file.
     * The parent directory's primary key. Represents the parent-child relationship.
     * root is -1;
     */
    protected long parentNo;
    
    /**
     * The owner of the directory(file)
     */
    protected int ownerNo;

   /**
     * The date and time when the folder or file was created.
     */
    protected Timestamp creation;

    /**
     * The date and time when the folder or file was last modified.
     */
    protected Timestamp lastModified;
    

}
