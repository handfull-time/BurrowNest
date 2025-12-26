package com.utime.burrowNest.dropbox.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DropboxTokenVO {
	/**
	 * 오류 발생 시 리턴되는 코드 값입니다. 정상 처리 시에는 존재하지 않습니다.
	 */
	String error;
	/**
	 * 오류 발생 시 리턴되는 설명 메시지입니다. 정상 처리 시에는 존재하지 않습니다.
	 */
	String errorDescription;
	/**
	 * 실제 API 호출 시 Authorization: Bearer <token> 헤더에 사용하는 값입니다. Dropbox 정책 변경으로 약 4시간(14400초) 동안만 유효합니다.
	 */
	String accessToken;
	/**
	 * 항상 bearer 고정입니다.
	 */
	String tokenType;
	/**
	 * [TTL] 토큰의 유효 기간(초)입니다. 보통 14400(4시간)이 리턴됩니다. 이 시간을 계산하여 만료 전 갱신 로직을 트리거해야
	 * 합니다.
	 */
	long expiresIn;
	/**
	 * [Critical/Long-lived] Access Token이 만료되었을 때, 사용자 개입 없이 새 토큰을 발급받기 위한 키입니다. 이
	 * 값은 절대 변하지 않으므로 DB에 암호화하여 영구 저장해야 합니다. (offline_access 스코프 필수)
	 */
	String refreshToken;
	/**
	 * 현재 발급된 토큰이 가진 권한 목록입니다. 공백(space)으로 구분됩니다.
	 */
	String scope;
	/**
	 * (Deprecated) 레거시 숫자형 사용자 ID입니다. 호환성을 위해 제공되나, 향후 account_id 사용을 권장합니다.
	 */
	String uid;
	/**
	 * [Primary Key] Dropbox 사용자의 고유 식별자입니다. (dbid:... 형태). 로컬 DB의 사용자 테이블과 매핑할 때 이 값을 Foreign Key로 사용하십시오.
	 */
	String accountId;
}