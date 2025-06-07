package com.utime.burrowNest.storage.vo;

import java.time.LocalDateTime;

import com.utime.burrowNest.common.util.BurrowUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a video file with metadata such as duration, resolution, frame rate,
 * and codec information. This class extends the {@link AbsBnFileInfo} abstract class.
 */
@Getter
@Setter
public class BnFileDocument extends AbsBnFileInfo {

	/**
	 * The title of the document.
	 * 문서 제목
	 */
	private String title;
	
	/**
	 * The subject or theme of the document.
	 * 문서 주제
	 */
	private String subject;
	
	/**
	 * The creator or author of the document.
	 * 문서를 작성한 사람
	 */
	private String creator;
	
	/**
	 * The name of the person who last modified the document.
	 * 마지막으로 문서를 수정한 사람
	 */
	private String lastModifiedBy;
	
	/**
	 * The date and time the document was created.
	 * 문서가 생성된 날짜 및 시간
	 */
	private LocalDateTime createDate;
	
	/**
	 * The date and time the document was last modified.
	 * 문서가 마지막으로 수정된 날짜 및 시간
	 */
	private LocalDateTime modifyDate;
	
	/**
	 * A set of keywords associated with the document.
	 * 문서에 연결된 키워드들
	 */
	private String keywords;
	
	/**
	 * A textual description of the document.
	 * 문서에 대한 설명
	 */
	private String description;
	
	/**
	 * Structured information describing the heading pairs in the document.
	 * 문서의 헤딩 페어(구조적 정보, 목차 등)
	 */
	private String headingPairs;
	
	/**
	 * The company associated with the document or its author.
	 * 문서나 작성자와 관련된 회사 정보
	 */
	private String company;
	
	@Override
	public String toString() {
		return BurrowUtils.toJson(this);
	}

}