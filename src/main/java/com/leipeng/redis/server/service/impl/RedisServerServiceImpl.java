package com.leipeng.redis.server.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.leipeng.redis.server.service.RedisServerService;
import com.leipeng.redis.server.service.util.Redis;

@Service("redisServerService")
public class RedisServerServiceImpl implements RedisServerService {

	@Resource(name = "objRedisTemplate")
	private RedisTemplate<String, Object> redisTemplate;

	@Resource(name = "valueSerializer")
	private GenericJackson2JsonRedisSerializer valueSerializer;

	@Resource(name = "stringSerializer")
	private StringRedisSerializer keySerializer;

	// key - value
	@Override
	public void set(String key, Object value) {
		set(key, value);
	}

	@Override
	public void set(String key, Object value, Long expireTime, TimeUnit unit) {
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
	public void multiSet(Map<String, Object> pairs) {
		multiSet(pairs, null, null);
	}

	@Override
	public void multiSet(Map<String, Object> pairs, Long expireTime, TimeUnit unit) {
		if (pairs == null || pairs.size() <= 0) {
			return;
		}
		final Long finalExpireTime = Redis.convert(expireTime, unit);

		if (finalExpireTime == null || finalExpireTime.longValue() <= 0) {
			redisTemplate.opsForValue().multiSet(pairs);
			return;
		}

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

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> clazz) {
		return (T) redisTemplate.opsForValue().get(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> multiGet(Collection<String> keys, Class<T> clazz) {
		List<T> ret = (List<T>) redisTemplate.opsForValue().multiGet(keys);
		return ret;
	}

	// set
	@Override
	public void sadd(String key, Object value) {
		sadd(key, value, null, null);
	}

	@Override
	public void sadd(String key, Object value, Long expireTime, TimeUnit unit) {
		if (key == null || StringUtils.isEmpty(key.toString()) || value == null
				|| StringUtils.isEmpty(value.toString())) {
			return;
		}
		final Long finalExpireTime = Redis.convert(expireTime, unit);
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
	public void sadd(String key, Object[] values) {
		sadd(key, values, null, null);
	}

	@Override
	public void sadd(String key, Object[] values, Long expireTime, TimeUnit unit) {
		if (key == null || StringUtils.isEmpty(key.toString()) || values == null || values.length <= 0) {
			return;
		}
		final Long finalExpireTime = Redis.convert(expireTime, unit);

		if (finalExpireTime == null || finalExpireTime.longValue() <= 0) {
			redisTemplate.opsForSet().add(key, values);
		} else {
			redisTemplate.executePipelined(new RedisCallback<Object>() {
				@Override
				public Object doInRedis(RedisConnection connection) throws DataAccessException {
					for (Object value : values) {
						connection.sAdd(keySerializer.serialize(key.toString()), valueSerializer.serialize(value));
					}
					connection.expire(keySerializer.serialize(key.toString()), finalExpireTime);
					return null;
				}
			});
		}
	}

	// List
	@Override
	public long leftPush(String key, Object value) {
		return leftPush(key, value, null, null);
	}

	@Override
	public long leftPush(String key, Object value, Long expireTime, TimeUnit unit) {
		if (key == null || StringUtils.isEmpty(key) || value == null || StringUtils.isEmpty(value.toString())) {
			return 0L;
		}
		if (unit == null) {
			unit = TimeUnit.MILLISECONDS;
		}

		Long count = 0L;

		Long finalExpireTime = Redis.convert(expireTime, unit);
		if (finalExpireTime == null) {
			count = redisTemplate.opsForList().leftPush(key, value);
		} else {
			RedisCallback<Long> action = new RedisCallback<Long>() {
				@Override
				public Long doInRedis(RedisConnection connection) throws DataAccessException {
					Long result = connection.lPush(keySerializer.serialize(key), valueSerializer.serialize(value));
					connection.expire(keySerializer.serialize(key), finalExpireTime);
					return result;
				}
			};
			count = Redis.execute(redisTemplate, action);
		}

		return count == null ? 0 : count.longValue();
	}

	@Override
	public long leftPushAll(String key, Object[] values) {
		return leftPush(key, values, null, null);
	}

	@Override
	public long leftPushAll(String key, Object[] values, Long expireTime, TimeUnit unit) {
		if (StringUtils.isEmpty(key) || values == null || values.length <= 0) {
			return 0;
		}

		if (unit == null) {
			unit = TimeUnit.MICROSECONDS;
		}

		Long count = 0L;

		final Long finalExpireSeconds = Redis.convert(expireTime, unit);
		if (finalExpireSeconds == null || finalExpireSeconds.longValue() <= 0) {
			count = redisTemplate.opsForList().leftPushAll(key, values);
		} else {
			RedisCallback<Long> action = new RedisCallback<Long>() {
				@Override
				public Long doInRedis(RedisConnection connection) throws DataAccessException {
					byte[][] valueBytes = new byte[values.length][];
					int i = 0;
					for (Object value : values) {
						valueBytes[i++] = valueSerializer.serialize(value);
					}
					Long result = connection.lPush(keySerializer.serialize(key), valueBytes);
					connection.expire(keySerializer.serialize(key), finalExpireSeconds);
					return result;
				}
			};
			count = Redis.execute(redisTemplate, action);
		}

		return count == null ? 0l : count.longValue();
	}

	public long rightPush(String key, Object value) {
		return leftPush(key, value, null, null);
	}

	@Override
	public long rightPush(String key, Object value, Long expireTime, TimeUnit unit) {
		if (key == null || StringUtils.isEmpty(key) || value == null || StringUtils.isEmpty(value.toString())) {
			return 0L;
		}
		if (unit == null) {
			unit = TimeUnit.MILLISECONDS;
		}

		Long count = 0L;

		Long finalExpireTime = Redis.convert(expireTime, unit);
		if (finalExpireTime == null) {
			count = redisTemplate.opsForList().rightPush(key, value);
		} else {
			RedisCallback<Long> action = new RedisCallback<Long>() {
				@Override
				public Long doInRedis(RedisConnection connection) throws DataAccessException {
					Long result = connection.rPush(keySerializer.serialize(key), valueSerializer.serialize(value));
					connection.expire(keySerializer.serialize(key), finalExpireTime);
					return result;
				}
			};
			count = Redis.execute(redisTemplate, action);
		}

		return count == null ? 0 : count.longValue();
	}

	@Override
	public long rightPushAll(String key, Object[] values) {
		return leftPush(key, values, null, null);
	}

	@Override
	public long rightPushAll(String key, Object[] values, Long expireTime, TimeUnit unit) {
		if (StringUtils.isEmpty(key) || values == null || values.length <= 0) {
			return 0;
		}

		if (unit == null) {
			unit = TimeUnit.MICROSECONDS;
		}

		Long count = 0L;

		final Long finalExpireSeconds = Redis.convert(expireTime, unit);
		if (finalExpireSeconds == null || finalExpireSeconds.longValue() <= 0) {
			count = redisTemplate.opsForList().rightPushAll(key, values);
		} else {
			RedisCallback<Long> action = new RedisCallback<Long>() {
				@Override
				public Long doInRedis(RedisConnection connection) throws DataAccessException {
					byte[][] valueBytes = new byte[values.length][];
					int i = 0;
					for (Object value : values) {
						valueBytes[i++] = valueSerializer.serialize(value);
					}
					Long result = connection.rPush(keySerializer.serialize(key), valueBytes);
					connection.expire(keySerializer.serialize(key), finalExpireSeconds);
					return result;
				}
			};
			count = Redis.execute(redisTemplate, action);
		}

		return count == null ? 0l : count.longValue();
	}

	@Override
	public long llength(String key) {
		if (StringUtils.isEmpty(key)) {
			return 0L;
		}
		return redisTemplate.opsForList().size(key);
	}

	@Override
	public <T> List<T> range(String key, Class<T> clazz) {		
		return range(key, clazz, 1, llength(key));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> range(String key, Class<T> clazz, long start, long end) {
		if(StringUtils.isEmpty(key) || clazz == null || start < end) {
			return new ArrayList<T>();
		}
		return (List<T>) redisTemplate.opsForList().range(key, start, end);
	}

	@Override
	public <T> List<T> leftPop(String key) {
		return null;
	}
	
	
}
