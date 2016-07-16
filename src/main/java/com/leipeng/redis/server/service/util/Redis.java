package com.leipeng.redis.server.service.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.StringUtils;

public class Redis {

	public static <K, V, T> T execute(RedisTemplate<K, V> redisTemplate, RedisCallback<T> action) {
		if (action == null) {
			return null;
		}

		return redisTemplate.execute(action, false, true);
	}

	public static Long convert(Long source, TimeUnit unit) {
		// 默认为毫秒，最终转化结果为秒
		if (source == null) {
			return null;
		}

		if (unit == null) {
			unit = TimeUnit.MILLISECONDS;
		}

		if (unit == TimeUnit.MILLISECONDS) {
			source = source / 1000;
		} else if (unit == TimeUnit.MINUTES) {
			source = source * 60;
		} else if (unit == TimeUnit.HOURS) {
			source = source * 60 * 60;
		} else if (unit == TimeUnit.DAYS) {
			source = source * 60 * 60 * 24;
		}

		if (source.longValue() <= 0) {
			return 1L;
		}

		return source.longValue();
	}

	// Object
	public static final <K, V> void set(RedisTemplate<K, V> redisTemplate, K key, V value, Long expireTime,
			TimeUnit unit) {
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

	public static final <K, V> void multiSet(RedisTemplate<K, V> redisTemplate, Map<? extends K, ? extends V> pairs,
			Long expireTime, TimeUnit unit) {
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
					connection.set(serializeKey(redisTemplate, key), serializeValue(redisTemplate, value));
					if (finalExpireTime != null && finalExpireTime.longValue() > 0) {
						connection.expire(serializeKey(redisTemplate, key), finalExpireTime);
					}
				}
				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public static <K, V, T> T get(RedisTemplate<K, V> redisTemplate, String key, Class<T> clazz) {
		return (T) redisTemplate.opsForValue().get(key);
	}

	@SuppressWarnings("unchecked")
	public static <K, V, T> List<T> multiGet(RedisTemplate<K, V> redisTemplate, Collection<K> keys, Class<T> clazz) {
		List<T> ret = (List<T>) redisTemplate.opsForValue().multiGet(keys);
		return ret;
	}

	// Set
	@SuppressWarnings("unchecked")
	public static <K, V> void sadd(RedisTemplate<K, V> redisTemplate, K key, V value, Long expireTime, TimeUnit unit) {
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
					connection.sAdd(serializeKey(redisTemplate, key), serializeValue(redisTemplate, value));
					connection.expire(serializeKey(redisTemplate, key), finalExpireTime);
					return null;
				}
			});
		}
	}

	public static <K, V> void saddAll(RedisTemplate<K, V> redisTemplate, K key, V[] values, Long expireTime,
			TimeUnit unit) {
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
					byte[][] valuesBytes = new byte[values.length][];
					int i = 0;
					for (V value : values) {
						valuesBytes[i++] = serializeValue(redisTemplate, value);
					}
					connection.sAdd(serializeKey(redisTemplate, key), valuesBytes);
					connection.expire(serializeKey(redisTemplate, key), finalExpireTime);
					return null;
				}
			});
		}
	}
	
	// List
	public static final <K, V> long leftPush(RedisTemplate<K, V> redisTemplate, K key, V value, Long expireTime, TimeUnit unit) {
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
					Long result = connection.lPush(serializeKey(redisTemplate, key), serializeValue(redisTemplate, value));
					connection.expire(serializeKey(redisTemplate, key), finalExpireTime);
					return result;
				}
			};
			count = execute(redisTemplate, action);
		}

		return count == null ? 0 : count.longValue();
	}
	
	public static final <K, V> long leftPushAll(RedisTemplate<K, V> redisTemplate, K key, V[] values, Long expireTime, TimeUnit unit) {
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
						valueBytes[i++] = serializeValue(redisTemplate, value);
					}
					Long result = connection.lPush(serializeKey(redisTemplate, key), valueBytes);
					connection.expire(serializeKey(redisTemplate, key), finalExpireSeconds);
					return result;
				}
			};
			count = Redis.execute(redisTemplate, action);
		}

		return count == null ? 0l : count.longValue();
	}

	public static final <K, V> long rightPush(RedisTemplate<K, V> redisTemplate, K key, V value, Long expireTime, TimeUnit unit) {
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
					Long result = connection.rPush(serializeKey(redisTemplate, key), serializeValue(redisTemplate, value));
					connection.expire(serializeKey(redisTemplate, key), finalExpireTime);
					return result;
				}
			};
			count = Redis.execute(redisTemplate, action);
		}

		return count == null ? 0 : count.longValue();
	}

	@SuppressWarnings("unchecked")
	public static final <K, V> long rightPushAll(RedisTemplate<K, V> redisTemplate, K key, V[] values, Long expireTime, TimeUnit unit) {
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
						valueBytes[i++] = serializeValue(redisTemplate, values);
					}
					Long result = connection.rPush(serializeKey(redisTemplate, key), valueBytes);
					connection.expire(serializeKey(redisTemplate, key), finalExpireSeconds);
					return result;
				}
			};
			count = Redis.execute(redisTemplate, action);
		}

		return count == null ? 0l : count.longValue();
	}
	
	public static final <K, V> long llength(RedisTemplate<K, V> redisTemplate, K key) {
		if (StringUtils.isEmpty(key)) {
			return 0L;
		}
		return redisTemplate.opsForList().size(key);
	}
	
	@SuppressWarnings("unchecked")
	public static final <K,V, T> List<T> range(RedisTemplate<K, V> redisTemplate, K key, Class<T> clazz, long start, long end) {
		if (StringUtils.isEmpty(key) || clazz == null || start < end) {
			return new ArrayList<T>();
		}
		return (List<T>) redisTemplate.opsForList().range(key, start, end);
	}
	
	public static final <K, V, T> T leftPop(RedisTemplate<K, V> redisTemplate, K key, Class<T> clazz) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}

		return (T) redisTemplate.opsForList().leftPop(key);
	}

	public static final <K, V, T> List<T> leftPop(RedisTemplate<K, V> redisTemplate, K key, Class<T> clazz, int num) {
		List<T> ret = new ArrayList<T>();
		if (StringUtils.isEmpty(key) || num <= 0) {
			return ret;
		}

		if (num == 1) {
			ret.add(leftPop(redisTemplate, key, clazz));
			return ret;
		}

		RedisCallback<List<T>> action = new RedisCallback<List<T>>() {
			@Override
			public List<T> doInRedis(RedisConnection connection) throws DataAccessException {
				List<T> ret = new ArrayList<T>();
				byte[] keyBytes = serializeKey(redisTemplate, key);

				for (int i = 0; i < num; i++) {
					byte[] value = connection.lPop(keyBytes);
					if (value == null || value.length <= 0) {
						break;
					}
					ret.add(deserializeValue(redisTemplate, value, clazz));
				}
				return ret;
			}
		};

		return Redis.execute(redisTemplate, action);
	}
	
	public static final <K, V, T> T rightPop(RedisTemplate<K, V> redisTemplate, K key, Class<T> clazz) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}

		return (T) redisTemplate.opsForList().rightPop(key);
	}

	public static final <K, V, T> List<T> rightPop(RedisTemplate<K, V> redisTemplate, K key, Class<T> clazz, int num) {
		List<T> ret = new ArrayList<T>();
		if (StringUtils.isEmpty(key) || num <= 0) {
			return ret;
		}

		if (num == 1) {
			ret.add(rightPop(redisTemplate, key, clazz));
			return ret;
		}

		RedisCallback<List<T>> action = new RedisCallback<List<T>>() {
			@Override
			public List<T> doInRedis(RedisConnection connection) throws DataAccessException {
				List<T> ret = new ArrayList<T>();
				byte[] keyBytes = serializeKey(redisTemplate, key);

				for (int i = 0; i < num; i++) {
					byte[] value = connection.rPop(keyBytes);
					if (value == null || value.length <= 0) {
						break;
					}
					ret.add(deserializeValue(redisTemplate, value, clazz));
				}
				return ret;
			}
		};

		return Redis.execute(redisTemplate, action);
	}

	// Zset	
	public static final <K, V> long zAdd(RedisTemplate<K, V> redisTemplate, K key, V value, double score, Long expireTime, TimeUnit unit) {
		if (StringUtils.isEmpty(key)) {
			return 0l;
		}

		if (expireTime == null || expireTime.longValue() <= 0) {
			return redisTemplate.opsForZSet().add(key, value, score) ? 1l : 0l;
		}

		unit = unit == null ? TimeUnit.MILLISECONDS : unit;

		final Long expireSeconds = Redis.convert(expireTime, unit);

		RedisCallback<Long> action = new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				byte[] keyBytes = serializeKey(redisTemplate, key);
				Long ret = connection.zAdd(keyBytes, score, serializeValue(redisTemplate, value)) ? 1L : 0L;
				connection.expire(keyBytes, expireSeconds);
				return ret;
			}
		};

		return Redis.execute(redisTemplate, action);
	}
	
	public static final <K, V> long zAddAll(RedisTemplate<K, V> redisTemplate, K key, Map<V, Double> tuples, Long expireTime, TimeUnit unit) {
		if (StringUtils.isEmpty(key) || tuples == null || tuples.size() <= 0) {
			return 0;
		}

		final Long expireSeconds = Redis.convert(expireTime, unit);

		RedisCallback<Long> action = new RedisCallback<Long>() {

			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				long ret = 0;

				byte[] keyBytes = serializeKey(redisTemplate, key);
				for (Entry<V, Double> tuple : tuples.entrySet()) {
					ret += (connection.zAdd(keyBytes, tuple.getValue().doubleValue(),
							serializeValue(redisTemplate, tuple.getKey())) ? 1 : 0);
				}

				if (expireSeconds != null && expireSeconds.longValue() > 0) {
					connection.expire(keyBytes, expireSeconds);
				}
				return ret;
			}
		};

		return Redis.execute(redisTemplate, action);
	}
	
	//Serializer
	@SuppressWarnings("unchecked")
	private static final <K, V> byte[] serializeKey(RedisTemplate<K, V> redisTemplate, Object key) {
		RedisSerializer serializer = redisTemplate.getKeySerializer();
		return serializer.serialize(key);
	}

	@SuppressWarnings("unchecked")
	private static final <K, V> byte[] serializeValue(RedisTemplate<K, V> redisTemplate, Object value) {
		RedisSerializer serializer = redisTemplate.getValueSerializer();
		return serializer.serialize(value);
	}
	
	@SuppressWarnings("unchecked")
	private static final <K, V, T> T deserializeValue(RedisTemplate<K, V> redisTemplate, byte[] value, Class<T> clazz) {
		RedisSerializer serializer = redisTemplate.getValueSerializer();
		return (T) serializer.deserialize(value);
	}
}
