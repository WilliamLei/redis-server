package com.leipeng.redis.server.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface NewRedisService {

	// base key-value
	void set(Object key, Object value);

	void set(Object key, Object value, Long timeout, TimeUnit unit);

	void multiSet(Map<Object, Object> tuples);

	void multiSet(Map<Object, Object> tuples, Long timeout, TimeUnit unit);

	String getString(Object key);

	Byte getByte(Object key);

	byte getByteValue(Object key);

	Integer getInteger(Object key);

	int getIntegerValue(Object key);

	Long getLong(Object key);

	long getLongValue(Object key);

	Double getDouble(Object key);

	double getDoubleValue(Object key);

	BigDecimal getBigDecimal(Object key);

	double getBigDecimalValue(Object key);

	<T> T getObj(Object key, Class<T> clazz);

	List<String> multiGetString(Collection<Object> keys);

	<T> List<T> multiGetObject(Collection<Object> keys, Class<T> clazz);

	// list
	void leftPush(Object key, Object value);

	void leftPush(Object key, Object value, Long timeout, TimeUnit unit);

	void leftPushAll(Object key, Object[] values);

	void leftPushAll(Object key, Object[] values, Long timeout, TimeUnit unit);

	void rightPush(Object key, Object value);

	void rightPush(Object key, Object value, Long timeout, TimeUnit unit);

	void rightPushAll(Object key, Object[] values);

	void rightPushAll(Object key, Object[] values, Long timeout, TimeUnit unit);

	Byte leftPopByte(Object key);

	List<Byte> leftPopBytes(Object key, int num);

	Integer leftPopInteger(Object key);

	List<Integer> leftPopIntegers(Object key, int num);

	Long leftPopLong(Object key);

	List<Long> leftPopLongs(Object key, int num);

	Double leftPopDouble(Object key);

	List<Double> leftPopDoubles(Object key, int num);

	BigDecimal leftPopBigDecimal(Object key);

	List<BigDecimal> leftPopBigDecimals(Object key, int num);

	<T> T leftPopObject(Object key, Class<T> clazz);

	<T> List<T> leftPopObjects(Object key, Class<T> clazz, int num);

	Byte rightleftPopByte(Object key);

	List<Byte> rightPopBytes(Object key, int num);

	Integer rightPopInteger(Object key);

	List<Integer> rightPopIntegers(Object key, int num);

	Long rightPopLong(Object key);

	List<Long> rightPopLongs(Object key, int num);

	Double rightPopDouble(Object key);

	List<Double> rightPopDoubles(Object key, int num);

	BigDecimal rightPopBigDecimal(Object key);

	List<BigDecimal> rightPopBigDecimals(Object key, int num);

	<T> T rightPopObject(Object key, Class<T> clazz);

	<T> List<T> rightPopObjects(Object key, Class<T> clazz, int num);
}
