package com.leipeng.redis.server.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface NewRedisService<K, V> {

	// base key-value
	void set(K key, V value);

	void set(K key, V value, Long timeout, TimeUnit unit);

	void multiSet(Map<K, V> tuples);

	void multiSet(Map<K, V> tuples, Long timeout, TimeUnit unit);

	String getString(K key);

	Byte getByte(K key);

	byte getByteValue(K key);

	Integer getInteger(K key);

	int getIntegerValue(K key);

	Long getLong(K key);

	long getLongValue(K key);

	Double getDouble(K key);

	double getDoubleValue(K key);

	BigDecimal getBigDecimal(K key);

	double getBigDecimalValue(K key);

	<T> T getObj(K key, Class<T> clazz);

	List<String> multiGetString(Collection<K> keys);

	<T> List<T> multiGetObject(Collection<K> keys, Class<T> clazz);

	// list
	void leftPush(K key, V value);

	void leftPush(K key, V value, Long timeout, TimeUnit unit);

	void leftPushAll(K key, V[] values);

	void leftPushAll(K key, V[] values, Long timeout, TimeUnit unit);

	void rightPush(K key, V value);

	void rightPush(K key, V value, Long timeout, TimeUnit unit);

	void rightPushAll(K key, V[] values);

	void rightPushAll(K key, V[] values, Long timeout, TimeUnit unit);

	Byte leftPopByte(K key);

	List<Byte> leftPopBytes(K key, int num);

	Integer leftPopInteger(K key);

	List<Integer> leftPopIntegers(K key, int num);

	Long leftPopLong(K key);

	List<Long> leftPopLongs(K key, int num);

	Double leftPopDouble(K key);

	List<Double> leftPopDoubles(K key, int num);

	BigDecimal leftPopBigDecimal(K key);

	List<BigDecimal> leftPopBigDecimals(K key, int num);

	<T> T leftPopObject(K key, Class<T> clazz);

	<T> List<T> leftPopObjects(K key, Class<T> clazz, int num);

	Byte rightleftPopByte(K key);

	List<Byte> rightPopBytes(K key, int num);

	Integer rightPopInteger(K key);

	List<Integer> rightPopIntegers(K key, int num);

	Long rightPopLong(K key);

	List<Long> rightPopLongs(K key, int num);

	Double rightPopDouble(K key);

	List<Double> rightPopDoubles(K key, int num);

	BigDecimal rightPopBigDecimal(K key);

	List<BigDecimal> rightPopBigDecimals(K key, int num);

	<T> T rightPopObject(K key, Class<T> clazz);

	<T> List<T> rightPopObjects(K key, Class<T> clazz, int num);

	Byte getByteElementAt(K key, long index);

	byte getByteValueElementAt(K key, long index);

	Integer getIntegerElementAt(K key, long index);

	int getIntegerValueElementAt(K key, long index);

	Long getLongElementAt(K key, long index);

	long getLongValueElementAt(K key, long index);

	Double getDoubleElementAt(K key, long index);

	double getDoubleValueElementAt(K key, long index);

	BigDecimal getBigDecimalElementAt(K key, long index);

	double getBigDecimalValueElementAt(K key, long index);

	void delFromList(K key, V value);

	long lengthOfList(K key);

	// Set
	void sAdd(K key, V value);

	void sAdd(K key, V value, Long timeout, TimeUnit unit);

	void sAddAll(K key, Collection<V> values);

	void sAddAll(K key, Collection<V> values, Long timeout, TimeUnit unit);

	Byte randomByte(K key);

	byte randomByteValue(K key);

	Integer randomInteger(K key);

	int randomIntegerValue(K key);

	Long randomLong(K key);

	long randomLongValue(K key);

	Double randomDouble(K key);

	double randomDoubleValue(K key);

	BigDecimal randomBigDecimal(K key);

	double randomBigDecimalValue(K key);

	<T> T randomObject(K key, Class<T> clazz);

	List<Byte> randomBytes(K key, int num);

	List<Integer> randomIntegers(K key, int num);

	List<Long> randomLongs(K key, int num);

	List<Double> randomDoubles(K key, int num);

	List<BigDecimal> randomBigDecimals(K key, int num);

	<T> List<T> randomObjects(K key, int num, Class<T> clazz);

	List<Byte> randomDistinctBytes(K key, int num);

	List<Integer> randomDistinctIntegers(K key, int num);

	List<Long> randomDistinctLongs(K key, int num);

	List<Double> randomDistinctDoubles(K key, int num);

	List<BigDecimal> randomDistinctBigDecimals(K key, int num);

	<T> List<T> randomDistinctObjects(K key, int num, Class<T> clazz);

	Byte randomPopByte(K key);

	byte randomPopByteValue(K key);

	Integer randomPopInteger(K key);

	int randomPopIntegerValue(K key);

	Long randomPopLong(K key);

	long randomPopLongValue(K key);

	Double randomPopDouble(K key);

	double randomPopDoubleValue(K key);

	BigDecimal randomPopBigDecimal(K key);

	double randomPopBigDecimalValue(K key);

	<T> T randomPopObject(K key, Class<T> clazz);

	List<Byte> randomPopBytes(K key, int num);

	List<Integer> randomPopIntegers(K key, int num);

	List<Long> randomPopLongs(K key, int num);

	List<Double> randomPopDoubles(K key, int num);

	List<BigDecimal> randomPopBigDecimals(K key, int num);

	<T> List<T> randomPopObjects(K key, int num, Class<T> clazz);

	long sizeOfSet(K key);

	// Zset
	void zAdd(K key, V value, double score);

	void zAdd(K key, V value, double score, Long timeout, TimeUnit unit);

	void zAddAll(K key, Map<V, Double> tuples);

	void zAddAll(K key, Map<V, Double> tuples, Long timeout, TimeUnit unit);

	void delFromZSet(K key, V value);

	void delFromZSet(K key, Collection<V> values);

	List<Byte> zRangeBytes(K key, long start, long end);

	List<Integer> zRangeIntegers(K key, long start, long end);

	List<Long> zRangeLongs(K key, long start, long end);

	List<Double> zRangeDoubles(K key, long start, long end);

	List<BigDecimal> zRangeBigDecimals(K key, long start, long end);

	<T> List<T> zRangeObjects(K key, long start, long end, Class<T> clazz);

	Map<Byte, Double> zRangeBytesWithScore(K key, long start, long end);

	Map<Integer, Double> zRangeIntegersWithScore(K key, long start, long end);

	Map<Long, Double> zRangeLongsWithScore(K key, long start, long end);

	Map<Double, Double> zRangeDoublesWithScore(K key, long start, long end);

	Map<BigDecimal, Double> zRangeBigDecimalsWithScore(K key, long start, long end);

	<T> Map<T, Double> zRangeObjectsWithScore(K key, long start, long end, Class<T> clazz);

	List<Byte> zRevRangeBytes(K key, long start, long end);

	List<Integer> zRevRangeIntegers(K key, long start, long end);

	List<Long> zRevRangeLongs(K key, long start, long end);

	List<Double> zRevRangeDoubles(K key, long start, long end);

	List<BigDecimal> zRevRangeBigDecimals(K key, long start, long end);

	<T> List<T> zRevRangeObjects(K key, long start, long end, Class<T> clazz);

	Map<Byte, Double> zRevRangeBytesWithScore(K key, long start, long end);

	Map<Integer, Double> zRevRangeIntegersWithScore(K key, long start, long end);

	Map<Long, Double> zRevRangeLongsWithScore(K key, long start, long end);

	Map<Double, Double> zRevRangeDoublesWithScore(K key, long start, long end);

	Map<BigDecimal, Double> zRevRangeBigDecimalsWithScore(K key, long start, long end);

	<T> Map<T, Double> zRevRangeObjectsWithScore(K key, long start, long end, Class<T> clazz);

	List<Byte> zRangeBytesByScore(K key, double min, double max, long offset, long limit, boolean reverse);

	List<Integer> zRangeIntegersByScore(K key, double min, double max, long offset, long limit, boolean reverse);

	List<Long> zRangeLongByScore(K key, double min, double max, long offset, long limit, boolean reverse);

	List<Double> zRangeDoublesByScore(K key, double min, double max, long offset, long limit, boolean reverse);

	List<BigDecimal> zRangeBigDecimalsByScore(K key, double min, double max, long offset, long limit,
			boolean reverse);

	<T> List<T> zRangeObjectsByScore(K key, double min, double max, long offset, long limit, Class<T> clazz,
			boolean reverse);

	List<Byte> zRevRangeBytesByScore(K key, double min, double max, long offset, long limit, boolean reverse);

	List<Integer> zRevRangeIntegersByScore(K key, double min, double max, long offset, long limit,
			boolean reverse);

	List<Long> zRevRangeLongByScore(K key, double min, double max, long offset, long limit, boolean reverse);

	List<Double> zRevRangeDoublesByScore(K key, double min, double max, long offset, long limit, boolean reverse);

	List<BigDecimal> zRevRangeBigDecimalsByScore(K key, double min, double max, long offset, long limit,
			boolean reverse);

	<T> List<T> zRevRangeObjectsByScore(K key, double min, double max, long offset, long limit, Class<T> clazz,
			boolean reverse);

	Map<Byte, Double> zRevRangeBytesByScoreWithScore(K key, double min, double max, long offset, long limit,
			boolean reverse);

	Map<Integer, Double> zRevRangeIntegersByScoreWithScore(K key, double min, double max, long offset, long limit,
			boolean reverse);

	Map<Long, Double> zRevRangeLongsByScoreWithScore(K key, double min, double max, long offset, long limit,
			boolean reverse);

	Map<Double, Double> zRevRangeDoublesByScoreWithScore(K key, double min, double max, long offset, long limit,
			boolean reverse);

	Map<BigDecimal, Double> zRevRangeBigDecimalsByScoreWithScore(K key, double min, double max, long offset,
			long limit, boolean reverse);

	<T> Map<T, Double> zRevRangeObjectsByScoreWithScore(K key, double min, double max, long offset, long limit,
			Class<T> clazz, boolean reverse);

	double increaseScoreInZset(K key, V value, double delta);

	long sizeOfZSet(K key);

	long zRank(K key, V value);

	// Hash
	long put(K key, Object hashKey, Object value);

	long put(K key, Object hashKey, Object value, Long timeout, TimeUnit unit);

	long putAll(K key, Object hashKey, Map<Object, Object> value);

	long putAll(K key, Object hashKey, Map<Object, Object> value, Long timeout, TimeUnit unit);

	Byte hGetByte(K key, Object hashKey);

	Integer hGetInteger(K key, Object hashKey);

	Long hGetLong(K key, Object hashKey);

	Double hGetDouble(K key, Object hashKey);

	BigDecimal hGetBigDecimal(K key, Object hashKey);

	<T> T hGetObject(K key, Object hashKey, Class<T> clazz);

	<T> List<T> hMultiGetObjects(K key, Collection<Object> hashKeys, Class<T> clazz);

	<T> Map<Object, T> hGetAll(K key, Class<T> clazz);
	
	boolean hContainKey(K key, Object hashKey);
	
	int delFromHash(K key, Object hashKey);
	
	int delAllFromHash(K key, Object[] hashKeys);
	
	long sizeOfHash(K key);
}
