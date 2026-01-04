package com.utime.burrowNest.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 최초 필수 테이블 관련 Mapper
 */
@Mapper
public interface KeyValueMapper {
	
	/**
	 * Key-Value 테이블 생성
	 * @return
	 */
	int createKayValueTable();
	
	/**
	 * 값 조회
	 * @param k
	 * @return
	 */
	String getValue(@Param("key") String k);
	
	/**
	 * 값 설정
	 * @param k
	 * @param v
	 * @param expireSeconds
	 * @return
	 */
	int setValue(@Param("key") String k, @Param("value") String v, @Param("expireSeconds") int expireSeconds);
	
	/**
	 * 키 삭제
	 * @param k
	 * @return
	 */
	int deleteKey(@Param("key") String k);
	
	/**
	 * 키 만료 시간 설정
	 * @param k
	 * @param expireSeconds
	 * @return
	 */
	int setExpire(@Param("key") String k, @Param("expireSeconds") int expireSeconds);
	
	/**
	 * 키 만료 시간 조회
	 * @param k
	 * @return minutes
	 */
	int getExpire(@Param("key") String k);
	
	/**
	 * 만료된 키 삭제
	 * @return
	 */
	int removeExpire();

}