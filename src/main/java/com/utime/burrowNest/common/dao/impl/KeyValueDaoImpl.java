package com.utime.burrowNest.common.dao.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utime.burrowNest.common.dao.KeyValueDao;
import com.utime.burrowNest.common.mapper.CommonMapper;
import com.utime.burrowNest.common.mapper.KeyValueMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
class KeyValueDaoImpl implements KeyValueDao {

	final KeyValueMapper mapper;
	
	final CommonMapper common;
	
	final ObjectMapper objectMapper;
	
	final int NoneLimit = 0;
	
	@PostConstruct
	private void postCunstruct() {
		if( ! common.existTable("APP_KV") ) {
			mapper.createKayValueTable();
		}
	}

	@Scheduled(fixedDelay = 60 * 60 * 1000) // 1시간
	public void cleanupExpired() {
	    final int cnt = mapper.removeExpire();
	    if (cnt > 0) {
	        log.info("Expired keys removed: {}", cnt);
	    }
	}
	
	@Override
	public String getValue(String k) {

		return mapper.getValue(k);
	}

	@Override
	public <T> T getObject(String k, Class<T> cls) throws RuntimeException {
		
		final String obj = this.getValue(k);
		if( obj == null || obj.isEmpty() ) {
			log.info("Key[{}] not found.", k);
			return null;
		}
		
		try {
			return objectMapper.readValue(obj, cls);
		} catch (Exception e) {
			log.error("Invalid value for key. {}({}) - {}", k, cls.getName(), obj);
			throw new RuntimeException(e);
		}
	}

	@Override
	public int setValue(String k, String v) {

		return this.setValue(k, v, 0);
	}

	@Override
	public int setValue(String k, String v, int expireSeconds) {

		return mapper.setValue(k, v, expireSeconds);
	}

	@Override
	public int setObject(String k, Object v) {
		
		return this.setObject(k, v, 0);
	}

	@Override
	public int setObject(String k, Object v, int expireSeconds) {
		
		int result = 0;
		try {
			final String json = objectMapper.writeValueAsString(v);
			result = this.setValue(k, json, expireSeconds);
		} catch (Exception e) {
			log.error("setObject error:", e);
			result = -1;
		}
		
		return result;
	}

	@Override
	public int deleteKey(String k) {
		
		return mapper.deleteKey(k);
	}

	@Override
	public int setExpire(String k, int expireSeconds) {
		
		return mapper.setExpire(k, expireSeconds);
	}

	@Override
	public int getExpire(String k) {
		
		return mapper.getExpire(k);
	}

}
