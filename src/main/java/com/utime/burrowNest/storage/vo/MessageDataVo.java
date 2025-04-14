package com.utime.burrowNest.storage.vo;

import lombok.Data;

/**
 * Websocket으로 전달할 데이터 
 */
@Data
public class MessageDataVo {
	/** 내용 */
	String message;
	/** 총 수량, 진행 수량 */
	long total, progress;
	/** 완료 여부. true:끝, false:진행중 */
	boolean done;
}
