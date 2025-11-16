package com.utime.burrowNest.storage.vo;

import java.util.List;

import com.utime.burrowNest.common.util.BurrowUtils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasteItem {
    private String name;
    private boolean directory;
    
    private EStorageIOMode mode;
    private String target;
    private List<StorageIOItem> list;
    
    @Override
	public String toString() {
		return BurrowUtils.toJson(this);
	}
}
