package com.leipeng.redis.server.service;

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
}
