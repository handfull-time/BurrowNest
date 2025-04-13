package com.utime.burrowNest.storage.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a video file with metadata such as duration, resolution, frame rate,
 * and codec information. This class extends the {@link AbsBnFileInfor} abstract class.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class BnFileArchive extends AbsBnFileInfor {
    
}