package com.utime.burrowNest.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 최초 필수 테이블 관련 Mapper
 */
@Mapper
public interface CommonMapper {
	
	/**
	 * 테이블 존재 확인
	 * @return
	 */
	public boolean existTable(@Param("tableName") String name);
	
	/**
	 * 인덱스 생성
	 * @param indexName
	 * @param tableName
	 * @param columns
	 * @return
	 */
	public int createIndex(@Param("indexName") String indexName, @Param("tableName") String tableName, @Param("columns") String columns);

	/**
	 * 테이블 삭제
	 * @param string
	 * @return
	 */
	public int dropTable(@Param("tableName") String tableName);

	


}
