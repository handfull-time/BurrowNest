package com.utime.burrowNest.storage.vo;

import java.util.List;

import lombok.Data;

@Data
public class PasteRequest {
	private String type;
	private String fromPath;
	private String toPath;
	private List<PasteItem> items;
}
