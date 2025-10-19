package com.utime.burrowNest.storage.vo;

import java.util.List;

import com.utime.burrowNest.common.util.BurrowUtils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasteRequest {
	private String type;
	private String fromPath;
	private String toPath;
	private List<PasteItem> items;
	
	@Override
	public String toString() {
		return BurrowUtils.toJson(this);
	}
}
