
package com.leipeng.redis.server.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

	@Override
	public BigDecimal getBigDecimal(Object key) {
		return (BigDecimal) Redis.get(redisTemplate, key, Number.class);
	}

	@Override
	public double getBigDecimalValue(Object key) {
		BigDecimal val = getBigDecimal(key);
		return val == null ? 0 : val.doubleValue();
	}

	@Override
	public List<String> multiGetString(Collection<Object> keys) {
		List<String> ret = Redis.multiGet(redisTemplate, keys, String.class);
		return ret;
	}

	@Override
	public <T> T getObj(Object key, Class<T> clazz) {
		return Redis.get(redisTemplate, key, clazz);
	}

	@Override
	public <T> List<T> multiGetObject(Collection<Object> keys, Class<T> clazz) {
		return Redis.multiGet(redisTemplate, keys, clazz);
	}

	// list
	@Override
	public void leftPush(Object key, Object value) {
		leftPush(key, value, null, null);

	}

	@Override
	public void leftPush(Object key, Object value, Long timeout, TimeUnit unit) {
		Redis.leftPush(redisTemplate, key, value, timeout, unit);
	}

	@Override
	public void leftPushAll(Object key, Object[] values) {
		leftPushAll(key, values, null, null);
	}

	@Override
	public void leftPushAll(Object key, Object[] values, Long timeout, TimeUnit unit) {
		Redis.leftPushAll(redisTemplate, key, values, timeout, unit);
	}

	@Override
	public void rightPush(Object key, Object value) {
		rightPush(key, value, null, null);
	}

	@Override
	public void rightPush(Object key, Object value, Long timeout, TimeUnit unit) {
		Redis.rightPush(redisTemplate, key, value, timeout, unit);
	}

	@Override
	public void rightPushAll(Object key, Object[] values) {
		rightPushAll(key, values, null, null);
	}

	@Override
	public void rightPushAll(Object key, Object[] values, Long timeout, TimeUnit unit) {
		Redis.rightPushAll(redisTemplate, key, values, timeout, unit);
	}

	@Override
	public Byte leftPopByte(Object key) {
		return (Byte) Redis.leftPop(redisTemplate, key, Number.class);
	}

	@Override
	public List<Byte> leftPopBytes(Object key, int num) {
		List<Number> results = Redis.leftPop(redisTemplate, key, Number.class, num);
		List<Byte> ret = new ArrayList<Byte>();
		if (results != null && results.size() > 0) {
			for (Number result : results) {
				if (result == null) {
					break;
				}
				ret.add(result.byteValue());
			}
		}
		return ret;
	}

	@Override
	public Integer leftPopInteger(Object key) {
		return (Integer) Redis.leftPop(redisTemplate, key, Number.class);
	}

	@Override
	public List<Integer> leftPopIntegers(Object key, int num) {
		List<Number> results = Redis.leftPop(redisTemplate, key, Number.class, num);
		List<Integer> ret = new ArrayList<Integer>();
		if (results != null && results.size() > 0) {
			for (Number result : results) {
				if (result == null) {
					break;
				}
				ret.add(result.intValue());
			}
		}
		return ret;
	}

	@Override
	public Long leftPopLong(Object key) {
		return (Long) Redis.leftPop(redisTemplate, key, Number.class);
	}

	@Override
	public List<Long> leftPopLongs(Object key, int num) {
		List<Number> results = Redis.leftPop(redisTemplate, key, Number.class, num);
		List<Long> ret = new ArrayList<Long>();
		if (results != null && results.size() > 0) {
			for (Number result : results) {
				if (result == null) {
					break;
				}
				ret.add(result.longValue());
			}
		}
		return ret;
	}

	@Override
	public Double leftPopDouble(Object key) {
		return (Double) Redis.leftPop(redisTemplate, key, Number.class);
	}

	@Override
	public List<Double> leftPopDoubles(Object key, int num) {
		List<Number> results = Redis.leftPop(redisTemplate, key, Number.class, num);
		List<Double> ret = new ArrayList<Double>();
		if (results != null && results.size() > 0) {
			for (Number result : results) {
				if (result == null) {
					break;
				}
				ret.add(result.doubleValue());
			}
		}
		return ret;
	}

	@Override
	public BigDecimal leftPopBigDecimal(Object key) {
		return (BigDecimal) Redis.leftPop(redisTemplate, key, Number.class);
	}

	@Override
	public List<BigDecimal> leftPopBigDecimals(Object key, int num) {
		List<Number> results = Redis.leftPop(redisTemplate, key, Number.class, num);
		List<BigDecimal> ret = new ArrayList<BigDecimal>();
		if (results != null && results.size() > 0) {
			for (Number result : results) {
				if (result == null) {
					break;
				}
				ret.add((BigDecimal) result);
			}
		}
		return ret;
	}

	@Override
	public <T> T leftPopObject(Object key, Class<T> clazz) {
		return Redis.leftPop(redisTemplate, key, clazz);
	}

	@Override
	public <T> List<T> leftPopObjects(Object key, Class<T> clazz, int num) {
		return Redis.leftPop(redisTemplate, key, clazz, num);
	}

	@Override
	public Byte rightleftPopByte(Object key) {
		return (Byte) Redis.rightPop(redisTemplate, key, Number.class);
	}

	@Override
	public List<Byte> rightPopBytes(Object key, int num) {
		List<Number> results = Redis.rightPop(redisTemplate, key, Number.class, num);
		List<Byte> ret = new ArrayList<Byte>();
		if (results != null && results.size() > 0) {
			for (Number result : results) {
				if (result == null) {
					break;
				}
				ret.add(result.byteValue());
			}
		}
		return ret;
	}

	@Override
	public Integer rightPopInteger(Object key) {
		return (Integer) Redis.rightPop(redisTemplate, key, Number.class);
	}

	@Override
	public List<Integer> rightPopIntegers(Object key, int num) {
		List<Number> results = Redis.rightPop(redisTemplate, key, Number.class, num);
		List<Integer> ret = new ArrayList<Integer>();
		if (results != null && results.size() > 0) {
			for (Number result : results) {
				if (result == null) {
					break;
				}
				ret.add(result.intValue());
			}
		}
		return ret;
	}

	@Override
	public Long rightPopLong(Object key) {
		return (Long) Redis.rightPop(redisTemplate, key, Number.class);
	}

	@Override
	public List<Long> rightPopLongs(Object key, int num) {
		List<Number> results = Redis.rightPop(redisTemplate, key, Number.class, num);
		List<Long> ret = new ArrayList<Long>();
		if (results != null && results.size() > 0) {
			for (Number result : results) {
				if (result == null) {
					break;
				}
				ret.add(result.longValue());
			}
		}
		return ret;
	}

	@Override
	public Double rightPopDouble(Object key) {
		return (Double) Redis.rightPop(redisTemplate, key, Number.class);
	}

	@Override
	public List<Double> rightPopDoubles(Object key, int num) {
		List<Number> results = Redis.rightPop(redisTemplate, key, Number.class, num);
		List<Double> ret = new ArrayList<Double>();
		if (results != null && results.size() > 0) {
			for (Number result : results) {
				if (result == null) {
					break;
				}
				ret.add(result.doubleValue());
			}
		}
		return ret;
	}

	@Override
	public BigDecimal rightPopBigDecimal(Object key) {
		return (BigDecimal) Redis.rightPop(redisTemplate, key, Number.class);
	}

	@Override
	public List<BigDecimal> rightPopBigDecimals(Object key, int num) {
		List<Number> results = Redis.rightPop(redisTemplate, key, Number.class, num);
		List<BigDecimal> ret = new ArrayList<BigDecimal>();
		if (results != null && results.size() > 0) {
			for (Number result : results) {
				if (result == null) {
					break;
				}
				ret.add((BigDecimal) result);
			}
		}
		return ret;
	}

	@Override
	public <T> T rightPopObject(Object key, Class<T> clazz) {
		return Redis.rightPop(redisTemplate, key, clazz);
	}

	@Override
	public <T> List<T> rightPopObjects(Object key, Class<T> clazz, int num) {
		return Redis.rightPop(redisTemplate, key, clazz, num);
	}

	@Override
	public Byte getByteElementAt(Object key, long index) {
		return (Byte) Redis.elementAt(redisTemplate, key, index, Number.class);
	}

	@Override
	public byte getByteValueElementAt(Object key, long index) {
		Byte value = getByteElementAt(key, index);
		return value == null ? 0 : value.byteValue();
	}

	@Override
	public Long getLongElementAt(Object key, long index) {
		return (Long) Redis.elementAt(redisTemplate, key, index, Number.class);
	}

	@Override
	public long getLongValueElementAt(Object key, long index) {
		Long value = getLongElementAt(key, index);
		return value == null ? 0 : value.longValue();
	}

	@Override
	public Integer getIntegerElementAt(Object key, long index) {
		return (Integer) Redis.elementAt(redisTemplate, key, index, Number.class);
	}

	@Override
	public int getIntegerValueElementAt(Object key, long index) {
		Integer value = getIntegerElementAt(key, index);
		return value == null ? 0 : value.intValue();
	}

	@Override
	public Double getDoubleElementAt(Object key, long index) {
		return (Double) Redis.elementAt(redisTemplate, key, index, Number.class);
	}

	@Override
	public double getDoubleValueElementAt(Object key, long index) {
		Double value = getDoubleElementAt(key, index);
		return value == null ? 0 : value.doubleValue();
	}

	@Override
	public BigDecimal getBigDecimalElementAt(Object key, long index) {
		return (BigDecimal) Redis.elementAt(redisTemplate, key, index, Number.class);
	}

	@Override
	public double getBigDecimalValueElementAt(Object key, long index) {
		BigDecimal value = getBigDecimalElementAt(key, index);
		return value == null ? 0 : value.doubleValue();
	}

	@Override
	public void delFromList(Object key, Object value) {
		Redis.lRemove(redisTemplate, key, value, 1);
	}

	@Override
	public long lengthOfList(Object key) {
		return Redis.llength(redisTemplate, key);
	}

	@Override
	public void sAdd(Object key, Object value) {
		sAdd(key, value, null, null);
	}

	@Override
	public void sAdd(Object key, Object value, Long timeout, TimeUnit unit) {
		Redis.sadd(redisTemplate, key, value, timeout, unit);
	}

	@Override
	public void sAddAll(Object key, Collection<Object> values) {
		sAddAll(key, values, null, null);
	}

	@Override
	public void sAddAll(Object key, Collection<Object> values, Long timeout, TimeUnit unit) {
		Redis.saddAll(redisTemplate, key, values.toArray(), timeout, unit);
	}

	@Override
	public Byte randomByte(Object key) {
		List<Number> values = Redis.randomMembers(redisTemplate, key, 1, Number.class);
		return (values == null || values.size() <= 0) ? null : (Byte) values.get(0);
	}

	@Override
	public byte randomByteValue(Object key) {
		Byte value = randomByte(key);
		return value == null ? 0 : value.byteValue();
	}

	@Override
	public Integer randomInteger(Object key) {
		List<Number> values = Redis.randomMembers(redisTemplate, key, 1, Number.class);
		return (values == null || values.size() <= 0) ? null : (Integer) values.get(0);
	}

	@Override
	public int randomIntegerValue(Object key) {
		Integer value = randomInteger(key);
		return value == null ? 0 : value.intValue();
	}

	@Override
	public Long randomLong(Object key) {
		List<Number> values = Redis.randomMembers(redisTemplate, key, 1, Number.class);
		return (values == null || values.size() <= 0) ? null : (Long) values.get(0);
	}

	@Override
	public long randomLongValue(Object key) {
		Long value = randomLong(key);
		return value == null ? 0 : value.longValue();
	}

	@Override
	public Double randomDouble(Object key) {
		List<Number> values = Redis.randomMembers(redisTemplate, key, 1, Number.class);
		return (values == null || values.size() <= 0) ? null : (Double) values.get(0);
	}

	@Override
	public double randomDoubleValue(Object key) {
		Double value = randomDouble(key);
		return value == null ? 0 : value.doubleValue();
	}

	@Override
	public BigDecimal randomBigDecimal(Object key) {
		List<Number> values = Redis.randomMembers(redisTemplate, key, 1, Number.class);
		return (values == null || values.size() <= 0) ? null : (BigDecimal) values.get(0);
	}

	@Override
	public double randomBigDecimalValue(Object key) {
		BigDecimal value = randomBigDecimal(key);
		return value == null ? 0 : value.doubleValue();
	}

	@Override
	public <T> T randomObject(Object key, Class<T> clazz) {
		List<T> values = Redis.randomMembers(redisTemplate, key, 1, clazz);
		return (values == null || values.size() <= 0) ? null : values.get(0);
	}

	@Override
	public List<Byte> randomBytes(Object key, int num) {
		List<Number> temp = Redis.randomMembers(redisTemplate, key, num, Number.class);
		List<Byte> ret = new ArrayList<Byte>();
		
		if(temp == null || temp.size() <= 0) {
			for(Number value : temp) {
				if(value == null) {
					break;
				}
				ret.add(value.byteValue());
			}
		}
		
		return ret;
	}

	@Override
	public List<Integer> randomIntegers(Object key, int num) {
		List<Number> temp = Redis.randomMembers(redisTemplate, key, num, Number.class);
		List<Integer> ret = new ArrayList<Integer>();
		
		if(temp == null || temp.size() <= 0) {
			for(Number value : temp) {
				if(value == null) {
					break;
				}
				ret.add(value.intValue());
			}
		}
		
		return ret;
	}

	@Override
	public List<Long> randomLongs(Object key, int num) {
		List<Number> temp = Redis.randomMembers(redisTemplate, key, num, Number.class);
		List<Long> ret = new ArrayList<Long>();
		
		if(temp == null || temp.size() <= 0) {
			for(Number value : temp) {
				if(value == null) {
					break;
				}
				ret.add(value.longValue());
			}
		}
		
		return ret;
	}

	@Override
	public List<Double> randomDoubles(Object key, int num) {
		List<Number> temp = Redis.randomMembers(redisTemplate, key, num, Number.class);
		List<Double> ret = new ArrayList<Double>();
		
		if(temp == null || temp.size() <= 0) {
			for(Number value : temp) {
				if(value == null) {
					break;
				}
				ret.add(value.doubleValue());
			}
		}
		
		return ret;
	}

	@Override
	public List<BigDecimal> randomBigDecimals(Object key, int num) {
		List<Number> temp = Redis.randomMembers(redisTemplate, key, num, Number.class);
		List<BigDecimal> ret = new ArrayList<BigDecimal>();
		
		if(temp == null || temp.size() <= 0) {
			for(Number value : temp) {
				if(value == null) {
					break;
				}
				ret.add((BigDecimal) value);
			}
		}
		
		return ret;
	}

	@Override
	public <T> List<T> randomObjects(Object key, int num, Class<T> clazz) {
		return Redis.randomMembers(redisTemplate, key, num, clazz);
	}

}
