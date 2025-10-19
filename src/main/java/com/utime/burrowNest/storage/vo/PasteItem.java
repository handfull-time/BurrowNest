package com.utime.burrowNest.storage.vo;

import com.utime.burrowNest.common.util.BurrowUtils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasteItem {
    private String name;
    private boolean directory;
    
    @Override
	public String toString() {
		return BurrowUtils.toJson(this);
	}
}
