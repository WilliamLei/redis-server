
package com.leipeng.redis.server.service.impl;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.leipeng.redis.server.service.NewRedisService;
import com.leipeng.redis.server.service.util.Redis;

@Service("redisServerService")
public class NewRedisServiceImpl implements NewRedisService {

	@Resource(name = "objRedisTemplate")
	private RedisTemplate<Object, Object> redisTemplate;

	
	@Override
	public void set(Object key, Object value) {
		set(key, value, null, null);
	}

	@Override
	public void set(Object key, Object value, Long timeout, TimeUnit unit) {
		Redis.set(redisTemplate, key, value, timeout, unit);
	}

	@Override
	public void multiSet(Map<Object, Object> tuples) {
		multiSet(tuples, null, null);
	}

	@Override
	public void multiSet(Map<Object, Object> tuples, Long timeout, TimeUnit unit) {
		Redis.multiSet(redisTemplate, tuples, timeout, unit);
	}

	@Override
	public String getString(Object key) {
		return Redis.get(redisTemplate, key, String.class);
	}

	@Override
	public Byte getByte(Object key) {
		return (Byte) Redis.get(redisTemplate, key, Number.class);
	}

	@Override
	public byte getByteValue(Object key) {
		Byte val = getByte(key);
		return val == null ? 0 : val.byteValue();
	}

	@Override
	public Integer getInteger(Object key) {
		return (Integer) Redis.get(redisTemplate, key, Number.class);
	}

	@Override
	public int getIntegerValue(Object key) {
		Integer val = getInteger(key);
		return val == null ? 0 : val.intValue();
	}

	@Override
	public Long getLong(Object key) {
		return (Long) Redis.get(redisTemplate, key, Number.class);
	}

	@Override
	public long getLongValue(Object key) {
		Long val = getLong(key);
		return val == null ? 0 : val.longValue();
	}

	@Override
	public Double getDouble(Object key) {
		return (Double) Redis.get(redisTemplate, key, Number.class);
	}

	@Override
	public double getDoubleValue(Object key) {
		Double val = getDouble(key);
		return val == null ? 0 : val.doubleValue();
	}
}
