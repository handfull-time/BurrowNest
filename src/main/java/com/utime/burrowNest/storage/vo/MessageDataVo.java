package com.utime.burrowNest.storage.vo;

import com.utime.burrowNest.common.util.BurrowUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * Websocket으로 전달할 데이터 
 */
@Setter
@Getter
public class MessageDataVo {
	/** 내용 */
	String message;
	/** 총 수량, 진행 수량 */
	long total, progress;
	/** 완료 여부. true:끝, false:진행중 */
	boolean done;
	
	@Override
	public String toString() {
		return BurrowUtils.toJson(this);
	}
}
