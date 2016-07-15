package com.leipeng.redis.server.service.util;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

public class Redis {
		
	public static <T> T execute(RedisTemplate<String, Object> redisTemplate, RedisCallback<T> action) {
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
}
