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

	Byte getByteElementAt(Object key, long index);

	byte getByteValueElementAt(Object key, long index);

	Integer getIntegerElementAt(Object key, long index);

	int getIntegerValueElementAt(Object key, long index);

	Long getLongElementAt(Object key, long index);

	long getLongValueElementAt(Object key, long index);

	Double getDoubleElementAt(Object key, long index);

	double getDoubleValueElementAt(Object key, long index);

	BigDecimal getBigDecimalElementAt(Object key, long index);

	double getBigDecimalValueElementAt(Object key, long index);

	void delFromList(Object key, Object value);

	long lengthOfList(Object key);

	// Set
	void sAdd(Object key, Object value);

	void sAdd(Object key, Object value, Long timeout, TimeUnit unit);

	void sAddAll(Object key, Collection<Object> values);

	void sAddAll(Object key, Collection<Object> values, Long timeout, TimeUnit unit);

	Byte randomByte(Object key);

	byte randomByteValue(Object key);

	Integer randomInteger(Object key);

	int randomIntegerValue(Object key);

	Long randomLong(Object key);

	long randomLongValue(Object key);

	Double randomDouble(Object key);

	double randomDoubleValue(Object key);

	BigDecimal randomBigDecimal(Object key);

	double randomBigDecimalValue(Object key);

	<T> T randomObject(Object key, Class<T> clazz);

	List<Byte> randomBytes(Object key, int num);

	List<Integer> randomIntegers(Object key, int num);

	List<Long> randomLongs(Object key, int num);

	List<Double> randomDoubles(Object key, int num);

	List<BigDecimal> randomBigDecimals(Object key, int num);

	<T> List<T> randomObjects(Object key, int num, Class<T> clazz);

	List<Byte> randomDistinctBytes(Object key, int num);

	List<Integer> randomDistinctIntegers(Object key, int num);

	List<Long> randomDistinctLongs(Object key, int num);

	List<Double> randomDistinctDoubles(Object key, int num);

	List<BigDecimal> randomDistinctBigDecimals(Object key, int num);

	<T> List<T> randomDistinctObjects(Object key, int num, Class<T> clazz);

	Byte randomPopByte(Object key);

	byte randomPopByteValue(Object key);

	Integer randomPopInteger(Object key);

	int randomPopIntegerValue(Object key);

	Long randomPopLong(Object key);

	long randomPopLongValue(Object key);

	Double randomPopDouble(Object key);

	double randomPopDoubleValue(Object key);

	BigDecimal randomPopBigDecimal(Object key);

	double randomPopBigDecimalValue(Object key);

	<T> T randomPopObject(Object key, Class<T> clazz);

	List<Byte> randomPopBytes(Object key, int num);

	List<Integer> randomPopIntegers(Object key, int num);

	List<Long> randomPopLongs(Object key, int num);

	List<Double> randomPopDoubles(Object key, int num);

	List<BigDecimal> randomPopBigDecimals(Object key, int num);

	<T> List<T> randomPopObjects(Object key, int num, Class<T> clazz);

	long sizeOfSet(Object key);

	// Zset
	void zAdd(Object key, Object value, double score);

	void zAdd(Object key, Object value, double score, Long timeout, TimeUnit unit);

	void zAddAll(Object key, Map<Object, Double> tuples);

	void zAddAll(Object key, Map<Object, Double> tuples, Long timeout, TimeUnit unit);

	void delFromZSet(Object key, Object value);

	void delFromZSet(Object key, Object[] values);

	List<Byte> zRangeBytes(Object key, long start, long end);

	List<Integer> zRangeIntegers(Object key, long start, long end);

	List<Long> zRangeLongs(Object key, long start, long end);

	List<Double> zRangeDoubles(Object key, long start, long end);

	List<BigDecimal> zRangeBigDecimals(Object key, long start, long end);

	<T> List<T> zRangeObjects(Object key, long start, long end, Class<T> clazz);

	Map<Byte, Double> zRangeBytesWithScore(Object key, long start, long end);

	Map<Integer, Double> zRangeIntegersWithScore(Object key, long start, long end);

	Map<Long, Double> zRangeLongsWithScore(Object key, long start, long end);

	Map<Double, Double> zRangeDoublesWithScore(Object key, long start, long end);

	Map<BigDecimal, Double> zRangeBigDecimalsWithScore(Object key, long start, long end);

	<T> Map<T, Double> zRangeObjectsWithScore(Object key, long start, long end, Class<T> clazz);

	List<Byte> zRevRangeBytes(Object key, long start, long end);

	List<Integer> zRevRangeIntegers(Object key, long start, long end);

	List<Long> zRevRangeLongs(Object key, long start, long end);

	List<Double> zRevRangeDoubles(Object key, long start, long end);

	List<BigDecimal> zRevRangeBigDecimals(Object key, long start, long end);

	<T> List<T> zRevRangeObjects(Object key, long start, long end, Class<T> clazz);

	Map<Byte, Double> zRevRangeBytesWithScore(Object key, long start, long end);

	Map<Integer, Double> zRevRangeIntegersWithScore(Object key, long start, long end);

	Map<Long, Double> zRevRangeLongsWithScore(Object key, long start, long end);

	Map<Double, Double> zRevRangeDoublesWithScore(Object key, long start, long end);

	Map<BigDecimal, Double> zRevRangeBigDecimalsWithScore(Object key, long start, long end);

	<T> Map<T, Double> zRevRangeObjectsWithScore(Object key, long start, long end, Class<T> clazz);

	List<Byte> zRangeBytesByScore(Object key, double min, double max, long offset, long limit, boolean reverse);

	List<Integer> zRangeIntegersByScore(Object key, double min, double max, long offset, long limit, boolean reverse);

	List<Long> zRangeLongByScore(Object key, double min, double max, long offset, long limit, boolean reverse);

	List<Double> zRangeDoublesByScore(Object key, double min, double max, long offset, long limit, boolean reverse);

	List<BigDecimal> zRangeBigDecimalsByScore(Object key, double min, double max, long offset, long limit,
			boolean reverse);

	<T> List<T> zRangeObjectsByScore(Object key, double min, double max, long offset, long limit, Class<T> clazz,
			boolean reverse);

	List<Byte> zRevRangeBytesByScore(Object key, double min, double max, long offset, long limit, boolean reverse);

	List<Integer> zRevRangeIntegersByScore(Object key, double min, double max, long offset, long limit,
			boolean reverse);

	List<Long> zRevRangeLongByScore(Object key, double min, double max, long offset, long limit, boolean reverse);

	List<Double> zRevRangeDoublesByScore(Object key, double min, double max, long offset, long limit, boolean reverse);

	List<BigDecimal> zRevRangeBigDecimalsByScore(Object key, double min, double max, long offset, long limit,
			boolean reverse);

	<T> List<T> zRevRangeObjectsByScore(Object key, double min, double max, long offset, long limit, Class<T> clazz,
			boolean reverse);

	Map<Byte, Double> zRevRangeBytesByScoreWithScore(Object key, double min, double max, long offset, long limit,
			boolean reverse);

	Map<Integer, Double> zRevRangeIntegersByScoreWithScore(Object key, double min, double max, long offset, long limit,
			boolean reverse);

	Map<Long, Double> zRevRangeLongsByScoreWithScore(Object key, double min, double max, long offset, long limit,
			boolean reverse);

	Map<Double, Double> zRevRangeDoublesByScoreWithScore(Object key, double min, double max, long offset, long limit,
			boolean reverse);

	Map<BigDecimal, Double> zRevRangeBigDecimalsByScoreWithScore(Object key, double min, double max, long offset,
			long limit, boolean reverse);

	<T> Map<T, Double> zRevRangeObjectsByScoreWithScore(Object key, double min, double max, long offset, long limit,
			Class<T> clazz, boolean reverse);

	double increaseScoreInZset(Object key, Object value, double delta);

	long sizeOfZSet(Object key);

	long zRank(Object key, Object value);

	// Hash
	long put(Object key, Object hashKey, Object value);

	long put(Object key, Object hashKey, Object value, Long timeout, TimeUnit unit);

	long putAll(Object key, Object hashKey, Map<Object, Object> value);

	long putAll(Object key, Object hashKey, Map<Object, Object> value, Long timeout, TimeUnit unit);

	Byte hGetByte(Object key, Object hashKey);

	Integer hGetInteger(Object key, Object hashKey);

	Long hGetLong(Object key, Object hashKey);

	Double hGetDouble(Object key, Object hashKey);

	BigDecimal hGetBigDecimal(Object key, Object hashKey);

	<T> T hGetObject(Object key, Object hashKey, Class<T> clazz);

	<T> List<T> hMultiGetObjects(Object key, Collection<Object> hashKeys, Class<T> clazz);

	<T> Map<Object, T> hGetAll(Object key, Class<T> clazz);
	
	boolean hContainKey(Object key, Object hashKey);
	
	int delFromHash(Object key, Object hashKey);
	
	int delAllFromHash(Object key, Object[] hashKeys);
	
	long sizeOfHash(Object key);
}
