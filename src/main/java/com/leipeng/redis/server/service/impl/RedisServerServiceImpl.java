package com.leipeng.redis.server.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.leipeng.redis.server.service.RedisServerService;

@Service("redisServerService")
public class RedisServerServiceImpl implements RedisServerService {

	@Resource(name = "objRedisTemplate")
	private RedisTemplate<Object, Object> redisTemplate;

	@Resource(name = "valueSerializer")
	private GenericJackson2JsonRedisSerializer valueSerializer;

	@Resource(name = "stringSerializer")
	private StringRedisSerializer keySerializer;
	
	// key - value
	@Override
	public void set(Object key, Object value) {
		set(key, value);
	}

	@Override
	public void set(Object key, Object value, Long expireTime, TimeUnit unit) {
		if (key == null || StringUtils.isEmpty(key.toString()) || value == null
				|| StringUtils.isEmpty(value.toString())) {
			return;
		}

		if (expireTime == null || expireTime.longValue() <= 0) {
			redisTemplate.opsForValue().set(key, value);
		} else {
			if (unit == null) {
				unit = TimeUnit.MILLISECONDS;
			}
			redisTemplate.opsForValue().set(key, value, expireTime, unit);
		}
	}

	@Override
	public void multiSet(Map<Object, Object> pairs) {
		multiSet(pairs, null, null);
	}

	@Override
	public void multiSet(Map<Object, Object> pairs, Long expireTime, TimeUnit unit) {
		if (pairs == null || pairs.size() <= 0) {
			return;
		}
		final Long finalExpireTime = convert(expireTime, unit);
		redisTemplate.executePipelined(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				for (Object key : pairs.keySet()) {
					Object value = pairs.get(key);
					if (key == null || StringUtils.isEmpty(key.toString()) || value == null
							|| StringUtils.isEmpty(value)) {
						continue;
					}
					connection.set(keySerializer.serialize(key.toString()), valueSerializer.serialize(value));
					if (finalExpireTime != null && finalExpireTime.longValue() > 0) {
						connection.expire(keySerializer.serialize(key.toString()), finalExpireTime);
					}
				}
				return null;
			}
		});
	}

	private Long convert(Long source, TimeUnit unit) {
		// 默认为毫秒，最终转化结果为秒
		if(source == null) {
			return null;
		}
		
		if(unit == null) {
			unit = TimeUnit.MILLISECONDS;
		}
		
		if(unit == TimeUnit.MILLISECONDS) {
			source = source / 1000;
		} else if (unit == TimeUnit.MINUTES) {
			source = source * 60;
		} else if (unit == TimeUnit.HOURS) {
			source = source * 60 * 60;
		} else if (unit == TimeUnit.DAYS) {
			source = source * 60 * 60 * 24;
		}
		
		if(source.longValue() <= 0) {
			return 1L;
		}
		
		return source.longValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> clazz) {
		return (T) redisTemplate.opsForValue().get(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> multiGet(Collection<Object> keys, Class<T> clazz) {
		List<T> ret = (List<T>) redisTemplate.opsForValue().multiGet(keys);
		return ret;
	}

	
	// set
	@Override
	public void sadd(Object key, Object value) {
		sadd(key, value, null, null);
	}

	@Override
	public void sadd(Object key, Object value, Long expireTime, TimeUnit unit) {
		if (key == null || StringUtils.isEmpty(key.toString()) || value == null
				|| StringUtils.isEmpty(value.toString())) {
			return;
		}
		final Long finalExpireTime = convert(expireTime, unit);
		if (expireTime == null || expireTime.longValue() <= 0) {
			redisTemplate.opsForSet().add(key, value);
		} else {
			redisTemplate.executePipelined(new RedisCallback<Object>() {
				@Override
				public Object doInRedis(RedisConnection connection) throws DataAccessException {
					connection.sAdd(keySerializer.serialize(key.toString()), valueSerializer.serialize(value));
					connection.expire(keySerializer.serialize(key.toString()), finalExpireTime);
					return null;
				}
			});
		}
	}

	@Override
	public void sadd(Object key, Object[] values) {
		sadd(key, values, null, null);
	}

	@Override
	public void sadd(Object key, Object[] values, Long expireTime, TimeUnit unit) {
		if (key == null || StringUtils.isEmpty(key.toString()) || values == null || values.length <= 0) {
			return;
		}
		final Long finalExpireTime = convert(expireTime, unit);
		if (expireTime == null || expireTime.longValue() <= 0) {
			redisTemplate.opsForSet().add(key, values);
		} else {
			redisTemplate.executePipelined(new RedisCallback<Object>() {
				@Override
				public Object doInRedis(RedisConnection connection) throws DataAccessException {
					for(Object value : values) {
						connection.sAdd(keySerializer.serialize(key.toString()), valueSerializer.serialize(value));
					}
					connection.expire(keySerializer.serialize(key.toString()), finalExpireTime);
					return null;
				}
			});
		}
	}

	@Override
	public long leftPush(Object key, Object value) {
		return leftPush(key, value, null, null);
	}

	@Override
	public long leftPush(Object key, Object value, Long expireTime, TimeUnit unit) {
		if (key == null || StringUtils.isEmpty(key.toString()) || value == null
				|| StringUtils.isEmpty(value.toString())) {
			return 0L;
		}
		if(unit == null) {
			unit = TimeUnit.MILLISECONDS;
		}
		long count = redisTemplate.opsForList().leftPush(key, value);
		
		if (expireTime != null && expireTime.longValue() > 0) {
			redisTemplate.expire(key, expireTime, unit);
		}
		return count;
	}

	
}
