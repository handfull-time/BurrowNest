package com.utime.burrowNest.storage.dto;

import java.util.List;

import lombok.Data;

@Data
public class DirContextResponse {
	private DirNodeDto node;
	private List<DirNodeDto> ancestors; // root → parent 순으로 보내도 되고, 필요에 맞춰 정의
	private List<DirNodeDto> children; // 즉시 표시용
	private List<String> breadcrumbs; // 문자열 경로
	private int effectiveFlags;
}
