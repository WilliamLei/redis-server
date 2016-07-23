
package com.leipeng.redis.server.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.leipeng.redis.server.service.RedisServerService;
import com.leipeng.redis.server.service.util.Redis;

@Service("redisServerService")
public class RedisServerServiceImpl implements RedisServerService {

	@Resource(name = "objRedisTemplate")
	private RedisTemplate<String, Object> redisTemplate;
	
	
	// key - value
	@Override
	public void set(String key, Object value) {
		set(key, value, null, null);
	}

	@Override
	public void set(String key, Object value, Long expireTime, TimeUnit unit) {
		Redis.set(redisTemplate, key, value, expireTime, unit);
	}

	@Override
	public void multiSet(Map<String, Object> pairs) {
		multiSet(pairs, null, null);
	}

	@Override
	public void multiSet(Map<String, Object> pairs, Long expireTime, TimeUnit unit) {
		Redis.multiSet(redisTemplate, pairs, expireTime, unit);
	}

	@Override
	public <T> T get(String key, Class<T> clazz) {
		return Redis.get(redisTemplate, key, clazz);
	}

	@Override
	public <T> List<T> multiGet(Collection<String> keys, Class<T> clazz) {
		return Redis.multiGet(redisTemplate, keys, clazz);
	}

	// set
	@Override
	public void sadd(String key, Object value) {
		sadd(key, value, null, null);
	}

	@Override
	public void sadd(String key, Object value, Long expireTime, TimeUnit unit) {
		Redis.sadd(redisTemplate, key, value, expireTime, unit);
	}

	@Override
	public void sadd(String key, Object[] values) {
		sadd(key, values, null, null);
	}

	@Override
	public void sadd(String key, Object[] values, Long expireTime, TimeUnit unit) {
		Redis.saddAll(redisTemplate, key, values, expireTime, unit);
	}

	// List
	@Override
	public long leftPush(String key, Object value) {
		return leftPush(key, value, null, null);
	}

	@Override
	public long leftPush(String key, Object value, Long expireTime, TimeUnit unit) {
		return Redis.leftPush(redisTemplate, key, value, expireTime, unit);
	}

	@Override
	public long leftPushAll(String key, Object[] values) {
		return leftPush(key, values, null, null);
	}

	@Override
	public long leftPushAll(String key, Object[] values, Long expireTime, TimeUnit unit) {
		return Redis.leftPushAll(redisTemplate, key, values, expireTime, unit);
	}

	public long rightPush(String key, Object value) {
		return leftPush(key, value, null, null);
	}

	@Override
	public long rightPush(String key, Object value, Long expireTime, TimeUnit unit) {
		return Redis.rightPush(redisTemplate, key, value, expireTime, unit);
	}

	@Override
	public long rightPushAll(String key, Object[] values) {
		return leftPush(key, values, null, null);
	}

	@Override
	public long rightPushAll(String key, Object[] values, Long expireTime, TimeUnit unit) {
		return Redis.rightPushAll(redisTemplate, key, values, expireTime, unit);
	}

	@Override
	public long llength(String key) {
		return Redis.llength(redisTemplate, key);
	}

	@Override
	public <T> List<T> range(String key, Class<T> clazz) {
		return range(key, clazz, 1, llength(key));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> range(String key, Class<T> clazz, long start, long end) {
		if (StringUtils.isEmpty(key) || clazz == null || start < end) {
			return new ArrayList<T>();
		}
		return (List<T>) redisTemplate.opsForList().range(key, start, end);
	}

	@Override
	public <T> T leftPop(String key, Class<T> clazz) {
		return Redis.leftPop(redisTemplate, key, clazz);
	}

	@Override
	public <T> List<T> leftPop(String key, Class<T> clazz, int num) {
		return Redis.leftPop(redisTemplate, key, clazz, num);
	}

	@Override
	public <T> T rightPop(String key, Class<T> clazz) {
		return Redis.rightPop(redisTemplate, key, clazz);
	}

	@Override
	public <T> List<T> rightPop(String key, Class<T> clazz, int num) {
		return Redis.rightPop(redisTemplate, key, clazz, num);
	}

	// Zset
	@Override
	public long zAdd(String key, Object value, double score) {
		return zAdd(key, value, score, null, null);
	}

	@Override
	public long zAdd(String key, Object value, double score, Long expireTime, TimeUnit unit) {
		return Redis.zAdd(redisTemplate, key, value, score, expireTime, unit);
	}

	@Override
	public long zAddAll(String key, Map<Object, Double> tuples) {
		return zAddAll(key, tuples, null, null);
	}

	@Override
	public long zAddAll(String key, Map<Object, Double> tuples, Long expireTime, TimeUnit unit) {
		return Redis.zAddAll(redisTemplate, key, tuples, expireTime, unit);
	}

}
