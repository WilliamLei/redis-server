package com.leipeng.redis.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.leipeng.redis.server.service.NewRedisService;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "classpath*:/spring/app-context.xml" });
		NewRedisService redisServerService = context.getBean(NewRedisService.class);
	}
}
