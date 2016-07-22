package com.leipeng.redis.server.service.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

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

	@SuppressWarnings("unchecked")
	public static final <K, V, T extends V> Set<T> sDiff(RedisTemplate<K, V> redisTemplate, K key1, K key2,
			Class<T> clazz) {
		if (StringUtils.isEmpty(key1) || StringUtils.isEmpty(key2)) {
			return null;
		}
		return (Set<T>) redisTemplate.opsForSet().difference(key1, key2);
	}

	@SuppressWarnings("unchecked")
	public static final <K, V, T extends V> Set<T> sDiff(RedisTemplate<K, V> redisTemplate, K key,
			Collection<K> otherKeys, Class<T> clazz) {
		if (StringUtils.isEmpty(key) || otherKeys == null || otherKeys.size() <= 0) {
			return null;
		}
		return (Set<T>) redisTemplate.opsForSet().difference(key, otherKeys);
	}

	public static final <K, V, T extends V> long sDiffAndStore(RedisTemplate<K, V> redisTemplate, K key1, K key2,
			K storeKey) {

		if (StringUtils.isEmpty(key1) || StringUtils.isEmpty(key2) || StringUtils.isEmpty(storeKey)) {
			return 0;
		}

		Long ret = redisTemplate.opsForSet().differenceAndStore(key1, key2, storeKey);

		return ret == null ? 0 : ret.longValue();
	}

	public static final <K, V, T extends V> long sDiffAndStore(RedisTemplate<K, V> redisTemplate, K key1,
			Collection<K> otherKeys, K storeKey) {

		if (StringUtils.isEmpty(key1) || otherKeys == null || otherKeys.size() <= 0 || StringUtils.isEmpty(storeKey)) {
			return 0;
		}

		Long ret = redisTemplate.opsForSet().differenceAndStore(key1, otherKeys, storeKey);

		return ret == null ? 0 : ret.longValue();
	}

	@SuppressWarnings("unchecked")
	public static final <K, V, T extends V> Set<T> sInter(RedisTemplate<K, V> redisTemplate, K key1, K key2,
			Class<T> clazz) {
		if (StringUtils.isEmpty(key1) || StringUtils.isEmpty(key2)) {
			return null;
		}
		return (Set<T>) redisTemplate.opsForSet().intersect(key1, key2);
	}

	@SuppressWarnings("unchecked")
	public static final <K, V, T extends V> Set<T> sInter(RedisTemplate<K, V> redisTemplate, K key1,
			Collection<K> otherKeys, Class<T> clazz) {
		if (StringUtils.isEmpty(key1) || otherKeys == null || otherKeys.size() <= 0) {
			return null;
		}

		return (Set<T>) redisTemplate.opsForSet().intersect(key1, otherKeys);
	}

	public static final <K, V, T extends V> long sInterAndStore(RedisTemplate<K, V> redisTemplate, K key1, K key2,
			K storeKey) {
		if (StringUtils.isEmpty(key1) || StringUtils.isEmpty(key2) || StringUtils.isEmpty(storeKey)) {
			return 0;
		}

		Long ret = redisTemplate.opsForSet().intersectAndStore(key1, key2, storeKey);

		return ret == null ? 0 : ret.longValue();
	}

	public static final <K, V, T extends V> long sInterAndStore(RedisTemplate<K, V> redisTemplate, K key1,
			Collection<K> otherKeys, K storeKey) {
		if (StringUtils.isEmpty(key1) || otherKeys == null || otherKeys.size() <= 0 || StringUtils.isEmpty(storeKey)) {
			return 0;
		}

		Long ret = redisTemplate.opsForSet().intersectAndStore(key1, otherKeys, storeKey);

		return ret == null ? 0 : ret.longValue();
	}

	public static final <K, V> boolean isMember(RedisTemplate<K, V> redisTemplate, K key, V value) {
		if (StringUtils.isEmpty(key) || value == null) {
			return false;
		}
		return redisTemplate.opsForSet().isMember(key, value);
	}

	@SuppressWarnings("unchecked")
	public static final <K, V, T extends V> Set<T> members(RedisTemplate<K, V> redisTemplate, K key, Class<T> clazz) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		return (Set<T>) redisTemplate.opsForSet().members(key);
	}

	@SuppressWarnings("unchecked")
	public static final <K, V, T extends V> List<T> randomMembers(RedisTemplate<K, V> redisTemplate, K key, int count,
			Class<T> clazz) {
		if (StringUtils.isEmpty(key) || count <= 0) {
			return null;
		}

		if (count == 1) {
			List<T> ret = new ArrayList<T>();
			ret.add((T) redisTemplate.opsForSet().randomMember(key));
			return ret;
		}

		return (List<T>) redisTemplate.opsForSet().randomMembers(key, count);
	}

	@SuppressWarnings("unchecked")
	public static final <K, V, T extends V> List<T> randomDistinctMembers(RedisTemplate<K, V> redisTemplate, K key,
			int count, Class<T> clazz) {
		if (StringUtils.isEmpty(key) || count <= 0) {
			return null;
		}

		if (count == 1) {
			List<T> ret = new ArrayList<T>();
			ret.add((T) redisTemplate.opsForSet().randomMember(key));
			return ret;
		}

		return (List<T>) redisTemplate.opsForSet().distinctRandomMembers(key, count);
	}

	public static final <K, V, T extends V> List<T> randomPopMembers(RedisTemplate<K, V> redisTemplate, K key,
			int count, Class<T> clazz) {
		if (StringUtils.isEmpty(key) || count <= 0) {
			return null;
		}
		List<T> ret = new ArrayList<T>();
		RedisCallback<List<T>> action = new RedisCallback<List<T>>() {
			@Override
			public List<T> doInRedis(RedisConnection connection) throws DataAccessException {
				List<T> ret = new ArrayList<T>();
				byte[] keyBytes = serializeKey(redisTemplate, key);
				for (int i = 0; i < count; i++) {
					byte[] value = connection.sPop(keyBytes);
					if (value == null || value.length <= 0) {
						break;
					}
					ret.add(deserializeValue(redisTemplate, value, clazz));
				}
				return ret;
			}
		};
		ret = execute(redisTemplate, action);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static final <K, V, T extends V> Set<T> union(RedisTemplate<K, V> redisTemplate, K key1, K key2,
			Class<T> clazz) {
		if (StringUtils.isEmpty(key1) || StringUtils.isEmpty(key2)) {
			return null;
		}

		return (Set<T>) redisTemplate.opsForSet().union(key1, key2);
	}

	@SuppressWarnings("unchecked")
	public static final <K, V, T extends V> Set<T> union(RedisTemplate<K, V> redisTemplate, K key1,
			Collection<K> otherKeys, Class<T> clazz) {
		if (StringUtils.isEmpty(key1) || otherKeys == null || otherKeys.size() <= 0) {
			return null;
		}

		return (Set<T>) redisTemplate.opsForSet().union(key1, otherKeys);
	}

	public static final <K, V, T extends V> long unionAndStore(RedisTemplate<K, V> redisTemplate, K key1, K key2,
			K storeKey) {
		if (StringUtils.isEmpty(key1) || StringUtils.isEmpty(key2) || StringUtils.isEmpty(storeKey)) {
			return 0;
		}

		Long ret = redisTemplate.opsForSet().unionAndStore(key1, key2, storeKey);

		return ret == null ? 0 : ret.longValue();
	}

	public static final <K, V, T extends V> long unionAndStore(RedisTemplate<K, V> redisTemplate, K key1,
			Collection<K> otherKeys, K storeKey) {
		if (StringUtils.isEmpty(key1) || otherKeys == null || otherKeys.size() <= 0 || StringUtils.isEmpty(storeKey)) {
			return 0;
		}

		Long ret = redisTemplate.opsForSet().unionAndStore(key1, otherKeys, storeKey);

		return ret == null ? 0 : ret.longValue();
	}

	public static final <K, V> long sRemove(RedisTemplate<K, V> redisTemplate, K key, V[] values) {
		if (StringUtils.isEmpty(key) || values == null || values.length <= 0) {
			return 0;
		}

		Long count = redisTemplate.opsForSet().remove(key, values);

		return count == null ? 0 : count.longValue();

	}

	public static final <K, V> long sCard(RedisTemplate<K, V> redisTemplate, K key) {
		if (StringUtils.isEmpty(key)) {
			return 0;
		}

		Long size = redisTemplate.opsForSet().size(key);

		return size == null ? 0 : size.longValue();
	}

	// List
	public static final <K, V> long leftPush(RedisTemplate<K, V> redisTemplate, K key, V value, Long expireTime,
			TimeUnit unit) {
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
					Long result = connection.lPush(serializeKey(redisTemplate, key),
							serializeValue(redisTemplate, value));
					connection.expire(serializeKey(redisTemplate, key), finalExpireTime);
					return result;
				}
			};
			count = execute(redisTemplate, action);
		}

		return count == null ? 0 : count.longValue();
	}

	public static final <K, V> long leftPushAll(RedisTemplate<K, V> redisTemplate, K key, V[] values, Long expireTime,
			TimeUnit unit) {
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

	public static final <K, V> long rightPush(RedisTemplate<K, V> redisTemplate, K key, V value, Long expireTime,
			TimeUnit unit) {
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
					Long result = connection.rPush(serializeKey(redisTemplate, key),
							serializeValue(redisTemplate, value));
					connection.expire(serializeKey(redisTemplate, key), finalExpireTime);
					return result;
				}
			};
			count = Redis.execute(redisTemplate, action);
		}

		return count == null ? 0 : count.longValue();
	}

	public static final <K, V> long rightPushAll(RedisTemplate<K, V> redisTemplate, K key, V[] values, Long expireTime,
			TimeUnit unit) {
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
						valueBytes[i++] = serializeValue(redisTemplate, value);
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
	public static final <K, V, T> List<T> range(RedisTemplate<K, V> redisTemplate, K key, Class<T> clazz, long start,
			long end) {
		if (StringUtils.isEmpty(key) || clazz == null || start < end) {
			return new ArrayList<T>();
		}
		return (List<T>) redisTemplate.opsForList().range(key, start, end);
	}

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
	public static final <K, V, T extends V> T elementAt(RedisTemplate<K, V> redisTemplate, K key, long index,
			Class<T> clazz) {
		if (StringUtils.isEmpty(key) || index < 0) {
			return null;
		}
		return (T) redisTemplate.opsForList().index(key, index);
	}

	public static final <K, V> long lRemove(RedisTemplate<K, V> redisTemplate, K key, V value, long count) {
		if (StringUtils.isEmpty(key) || value == null) {
			return 0;
		}
		Long ret = redisTemplate.opsForList().remove(key, count, value);
		return ret == null ? 0 : ret.longValue();
	}

	// Zset
	public static final <K, V> long zAdd(RedisTemplate<K, V> redisTemplate, K key, V value, double score,
			Long expireTime, TimeUnit unit) {
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

	public static final <K, V> long zAddAll(RedisTemplate<K, V> redisTemplate, K key, Map<V, Double> tuples,
			Long expireTime, TimeUnit unit) {
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

	@SuppressWarnings("unchecked")
	public static final <K, V, T> Set<T> zrange(RedisTemplate<K, V> redisTemplate, K key, long start, long end,
			Class<T> clazz, boolean reverse) {
		if (key == null || StringUtils.isEmpty(key) || start < end) {
			return new LinkedHashSet<T>();
		}
		if (reverse) {
			return (Set<T>) redisTemplate.opsForZSet().reverseRange(key, start, end);
		}
		return (Set<T>) redisTemplate.opsForZSet().range(key, start, end);
	}

	@SuppressWarnings("unchecked")
	public static final <K, V, T> Set<T> zrangeByScore(RedisTemplate<K, V> redisTemplate, K key, double min, double max,
			Class<T> clazz, boolean reverse) {
		if (key == null || StringUtils.isEmpty(key) || min > max) {
			return new LinkedHashSet<T>();
		}

		if (reverse) {
			return (Set<T>) redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
		}
		return (Set<T>) redisTemplate.opsForZSet().rangeByScore(key, min, max);
	}

	@SuppressWarnings("unchecked")
	public static final <K, V, T> Set<T> zrangeByScore(RedisTemplate<K, V> redisTemplate, K key, double min, double max,
			long offset, long limit, Class<T> clazz, boolean reverse) {
		if (StringUtils.isEmpty(key) || min > max) {
			return new LinkedHashSet<T>();
		}
		if (offset <= 0) {
			offset = 1;
		}
		if (reverse) {
			return (Set<T>) redisTemplate.opsForZSet().reverseRangeByScore(key, min, max, offset, limit);
		}
		return (Set<T>) redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, limit);
	}

	public static final <K, V, T> Set<TypedTuple<T>> zrangeWithScore(RedisTemplate<K, V> redisTemplate, K key,
			long start, long end, Class<T> clazz, boolean reverse) {
		if (key == null || StringUtils.isEmpty(key) || start < end) {
			return new LinkedHashSet<TypedTuple<T>>();
		}
		RedisCallback<Set<TypedTuple<T>>> action = new RedisCallback<Set<TypedTuple<T>>>() {
			@Override
			public Set<TypedTuple<T>> doInRedis(RedisConnection connection) throws DataAccessException {
				Set<TypedTuple<T>> ret = new LinkedHashSet<ZSetOperations.TypedTuple<T>>();
				Set<Tuple> tuples = null;
				if (reverse) {
					tuples = connection.zRevRangeWithScores(serializeKey(redisTemplate, key), start, end);
				} else {
					tuples = connection.zRangeWithScores(serializeKey(redisTemplate, key), start, end);
				}

				if (tuples != null && tuples.size() > 0) {
					for (Tuple tuple : tuples) {
						ret.add(new DefaultTypedTuple<T>(deserializeValue(redisTemplate, tuple.getValue(), clazz),
								tuple.getScore()));
					}
				}
				return ret;
			}
		};
		return execute(redisTemplate, action);
	}

	public static final <K, V, T> Set<TypedTuple<T>> zrangeByScoreWithScore(RedisTemplate<K, V> redisTemplate, K key,
			double min, double max, Class<T> clazz, boolean reverse) {
		if (StringUtils.isEmpty(key) || min > max) {
			return new LinkedHashSet<TypedTuple<T>>();
		}

		RedisCallback<Set<TypedTuple<T>>> action = new RedisCallback<Set<TypedTuple<T>>>() {
			@Override
			public Set<TypedTuple<T>> doInRedis(RedisConnection connection) throws DataAccessException {
				Set<TypedTuple<T>> ret = new LinkedHashSet<ZSetOperations.TypedTuple<T>>();
				Set<Tuple> tuples = null;
				if (reverse) {
					tuples = connection.zRevRangeByScoreWithScores(serializeKey(redisTemplate, key), min, max);
				} else {
					tuples = connection.zRangeByScoreWithScores(serializeKey(redisTemplate, key), min, max);
				}

				if (tuples != null && tuples.size() > 0) {
					for (Tuple tuple : tuples) {
						ret.add(new DefaultTypedTuple<T>(deserializeValue(redisTemplate, tuple.getValue(), clazz),
								tuple.getScore()));
					}
				}
				return ret;
			}
		};

		return execute(redisTemplate, action);
	}

	public static final <K, V, T> Set<TypedTuple<T>> zrangeByScoreWithScore(RedisTemplate<K, V> redisTemplate, K key,
			double min, double max, long offset, long limit, Class<T> clazz, boolean reverse) {
		if (StringUtils.isEmpty(key) || min > max) {
			return new LinkedHashSet<TypedTuple<T>>();
		}

		RedisCallback<Set<TypedTuple<T>>> action = new RedisCallback<Set<TypedTuple<T>>>() {
			@Override
			public Set<TypedTuple<T>> doInRedis(RedisConnection connection) throws DataAccessException {
				Set<TypedTuple<T>> ret = new LinkedHashSet<ZSetOperations.TypedTuple<T>>();
				Set<Tuple> tuples = null;
				if (reverse) {
					tuples = connection.zRevRangeByScoreWithScores(serializeKey(redisTemplate, key), min, max, offset,
							limit);
				} else {
					tuples = connection.zRangeByScoreWithScores(serializeKey(redisTemplate, key), min, max, offset,
							limit);
				}

				if (tuples != null && tuples.size() > 0) {
					for (Tuple tuple : tuples) {
						ret.add(new DefaultTypedTuple<T>(deserializeValue(redisTemplate, tuple.getValue(), clazz),
								tuple.getScore()));
					}
				}
				return ret;
			}
		};

		return execute(redisTemplate, action);
	}

	public static final <K, V> double zIncrBy(RedisTemplate<K, V> redisTemplate, K key, V value, double delta) {
		if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
			return 0;
		}
		Double ret = redisTemplate.opsForZSet().incrementScore(key, value, delta);
		return ret == null ? 0 : ret.doubleValue();
	}

	public static final <K, V> long zRemove(RedisTemplate<K, V> redisTemplate, K key, V[] values) {
		if (StringUtils.isEmpty(key) || StringUtils.isEmpty(values)) {
			return 0;
		}
		Long ret = redisTemplate.opsForZSet().remove(key, values);
		return ret == null ? 0 : ret.longValue();
	}

	public static final <K, V> long zCard(RedisTemplate<K, V> redisTemplate, K key) {
		if (StringUtils.isEmpty(key)) {
			return 0;
		}

		Long ret = redisTemplate.opsForZSet().zCard(key);
		return ret == null ? 0 : ret.longValue();
	}

	// Map
	public static final <K, V> long put(RedisTemplate<K, V> redisTemplate, K key, Object hashKey, V value, Long timeout,
			TimeUnit unit) {
		if (StringUtils.isEmpty(key) || value == null) {
			return 0;
		}

		if (unit == null) {
			unit = TimeUnit.MILLISECONDS;
		}

		Long finalTimeOut = convert(timeout, unit);

		Long ret = 1L;

		if (finalTimeOut == null || finalTimeOut.longValue() <= 0) {
			redisTemplate.opsForHash().put(key, hashKey, value);
		} else {
			RedisCallback<Long> action = new RedisCallback<Long>() {
				@Override
				public Long doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] rawKey = serializeKey(redisTemplate, key);
					long ret = connection.hSet(rawKey, serializeHashKey(redisTemplate, hashKey),
							serializeValue(redisTemplate, value)) ? 1 : 0;
					connection.expire(rawKey, finalTimeOut);
					return ret;
				}
			};
			ret = execute(redisTemplate, action);
		}

		return ret;
	}

	public static final <K, V> long putAll(RedisTemplate<K, V> redisTemplate, K key, Map<K, V> tuples, Long timeout,
			TimeUnit unit) {
		if (StringUtils.isEmpty(key) || tuples == null || tuples.size() <= 0) {
			return 0;
		}
		unit = unit == null ? TimeUnit.MILLISECONDS : unit;
		Long finalTimeout = convert(timeout, unit);
		Long ret = 0L;

		RedisCallback<Long> action = new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				Map<byte[], byte[]> values = new LinkedHashMap<byte[], byte[]>();
				for (Map.Entry<K, V> tuple : tuples.entrySet()) {
					values.put(serializeHashKey(redisTemplate, tuple.getKey()),
							serializeHashValue(redisTemplate, tuple.getValue()));
				}
				connection.hMSet(serializeKey(redisTemplate, key), values);
				if (finalTimeout != null && finalTimeout.longValue() > 0) {
					connection.expire(serializeKey(redisTemplate, key), finalTimeout);
				}
				return (long) tuples.size();
			}
		};

		ret = execute(redisTemplate, action);
		return ret == null ? 0 : ret.longValue();
	}

	@SuppressWarnings("unchecked")
	public static final <K, V, T> T hGet(RedisTemplate<K, V> redisTemplate, K key, Object hashKey, Class<T> clazz) {
		if (StringUtils.isEmpty(key) || StringUtils.isEmpty(hashKey)) {
			return null;
		}
		return (T) redisTemplate.opsForHash().get(key, hashKey);
	}

	@SuppressWarnings("unchecked")
	public static final <K, V, T> Map<Object, T> hGetAll(RedisTemplate<K, V> redisTemplate, K key, Class<T> clazz) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}

		return (Map<Object, T>) redisTemplate.opsForHash().entries(key);
	}

	@SuppressWarnings("unchecked")
	public static final <K, V, T> List<T> hMultiGet(RedisTemplate<K, V> redisTemplate, K key,
			Collection<Object> hashKeys, Class<T> clazz) {
		if (StringUtils.isEmpty(key) || hashKeys == null || hashKeys.size() <= 0) {
			return null;
		}

		List<T> result = (List<T>) redisTemplate.opsForHash().multiGet(key, hashKeys);
		result.removeAll(null);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static final <K, V, T> Set<T> hKeys(RedisTemplate<K, V> redisTemplate, K key, Class<T> clazz) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}

		return (Set<T>) redisTemplate.opsForHash().keys(key);
	}

	@SuppressWarnings("unchecked")
	public static final <K, V, T> Set<T> hValues(RedisTemplate<K, V> redisTemplate, K key, Class<T> clazz) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		
		return (Set<T>) redisTemplate.opsForHash().values(key); 
	}

	public static final <K, V> boolean hContainKey(RedisTemplate<K, V> redisTemplate, K key, Object hashKey) {
		if (StringUtils.isEmpty(key) || StringUtils.isEmpty(hashKey)) {
			return false;
		}
		return redisTemplate.opsForHash().hasKey(key, hashKey);
	}

	public static final <K, V> int hDel(RedisTemplate<K, V> redisTemplate, K key, Object[] hashKeys) {
		if (StringUtils.isEmpty(key) || hashKeys == null || hashKeys.length <= 0) {
			return 0;
		}
		redisTemplate.opsForHash().delete(key, hashKeys);
		return hashKeys.length;
	}

	public static final <K, V> long hLen(RedisTemplate<K, V> redisTemplate, K key) {
		if(StringUtils.isEmpty(key)) {
			return 0;
		}
		
		Long ret = redisTemplate.opsForHash().size(key);
		return ret == null ? 0 : ret.longValue();
	}
	
	// Serializer
	@SuppressWarnings("unchecked")
	private static final <K, V> byte[] serializeKey(RedisTemplate<K, V> redisTemplate, Object key) {
		RedisSerializer serializer = redisTemplate.getKeySerializer();
		serializer = serializer == null ? redisTemplate.getStringSerializer() : serializer;
		return serializer.serialize(key);
	}

	@SuppressWarnings("unchecked")
	private static final <K, V> byte[] serializeValue(RedisTemplate<K, V> redisTemplate, Object value) {
		RedisSerializer serializer = redisTemplate.getValueSerializer();
		serializer = serializer == null ? redisTemplate.getDefaultSerializer() : serializer;
		return serializer.serialize(value);
	}

	@SuppressWarnings("unchecked")
	private static final <K, V, T> T deserializeValue(RedisTemplate<K, V> redisTemplate, byte[] value, Class<T> clazz) {
		RedisSerializer serializer = redisTemplate.getValueSerializer();
		serializer = serializer == null ? redisTemplate.getDefaultSerializer() : serializer;
		return (T) serializer.deserialize(value);
	}

	@SuppressWarnings("unchecked")
	private static final <K, V> byte[] serializeHashKey(RedisTemplate<K, V> redisTemplate, Object hashKey) {
		RedisSerializer serializer = redisTemplate.getHashKeySerializer();
		serializer = serializer == null ? redisTemplate.getDefaultSerializer() : serializer;
		return serializer.serialize(hashKey);
	}

	@SuppressWarnings("unchecked")
	private static final <K, V> byte[] serializeHashValue(RedisTemplate<K, V> redisTemplate, Object hashValue) {
		RedisSerializer serializer = redisTemplate.getHashValueSerializer();
		serializer = serializer == null ? redisTemplate.getDefaultSerializer() : serializer;
		return serializer.serialize(hashValue);
	}
}
