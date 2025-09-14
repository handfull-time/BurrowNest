package com.utime.burrowNest.storage.dto;

import java.util.List;

import lombok.Data;

@Data
public class ChildrenResponse {
	private List<DirNodeDto> items;
	 private long total;

	 public ChildrenResponse() {}
	 public ChildrenResponse(List<DirNodeDto> items, long total) {
	     this.items = items; this.total = total;
	 }
}
