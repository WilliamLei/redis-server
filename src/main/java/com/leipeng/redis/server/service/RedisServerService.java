package com.leipeng.redis.server.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface RedisServerService {

	void set(Object key, Object value);

	void set(Object key, Object value, Long expireTime, TimeUnit unit);

	void multiSet(Map<Object, Object> pairs);

	void multiSet(Map<Object, Object> pairs, Long expireTime, TimeUnit unit);

	<T> T get(Object key, Class<T> clazz);

	<T> List<T> multiGet(Collection<Object> keys, Class<T> clazz);
	
	void sadd(Object key, Object value);
	
	void sadd(Object key, Object[] values);
	
	void sadd(Object key, Object value, Long expireTime, TimeUnit unit);
	
	void sadd(Object key, Object[] values, Long expireTime, TimeUnit unit);

	// list
	long leftPush(Object key, Object value);
	
	long leftPush(Object key, Object value, Long expireTime, TimeUnit unit);
	
}
