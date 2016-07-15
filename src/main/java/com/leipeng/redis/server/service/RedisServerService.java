package com.leipeng.redis.server.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;

public interface RedisServerService {

	void set(String key, Object value);

	void set(String key, Object value, Long expireTime, TimeUnit unit);

	void multiSet(Map<String, Object> pairs);

	void multiSet(Map<String, Object> pairs, Long expireTime, TimeUnit unit);

	<T> T get(String key, Class<T> clazz);

	<T> List<T> multiGet(Collection<String> keys, Class<T> clazz);

	void sadd(String key, Object value);

	void sadd(String key, Object[] values);

	void sadd(String key, Object value, Long expireTime, TimeUnit unit);

	void sadd(String key, Object[] values, Long expireTime, TimeUnit unit);

	// list
	long rightPush(String key, Object value);

	long rightPush(String key, Object value, Long expireTime, TimeUnit unit);

	long rightPushAll(String key, Object[] values);

	long rightPushAll(String key, Object[] values, Long expireTime, TimeUnit unit);

	long leftPush(String key, Object value);

	long leftPush(String key, Object value, Long expireTime, TimeUnit unit);

	long leftPushAll(String key, Object[] values);

	long leftPushAll(String key, Object[] values, Long expireTime, TimeUnit unit);

	long llength(String key);

	<T> List<T> range(String key, Class<T> clazz);

	<T> List<T> range(String key, Class<T> clazz, long start, long end);

	<T> T leftPop(String key, Class<T> clazz);

	<T> List<T> leftPop(String key, Class<T> clazz, int num);

	<T> T rightPop(String key, Class<T> clazz);

	<T> List<T> rightPop(String key, Class<T> clazz, int num);
	
	//Zset
	long zAdd(String key, Object value, double score);
	
	long zAdd(String key, Object value, double score, Long expireTime, TimeUnit unit);
	
	long zAddAll(String key, Map<Object, Double> tuples);
	
	long zAddAll(String key, Map<Object, Double> tuples, Long expireTime, TimeUnit unit);
	
}
