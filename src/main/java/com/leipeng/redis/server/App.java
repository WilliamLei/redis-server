package com.leipeng.redis.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.leipeng.redis.server.model.RedisModel;
import com.leipeng.redis.server.service.RedisServerService;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "classpath*:/spring/app-context.xml" });
		RedisServerService redisServerService = context.getBean(RedisServerService.class);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("4", new RedisModel("leipeng1", "leipeng1"));
		map.put("3", new RedisModel("leipeng2", "leipeng2"));
		redisServerService.multiSet(map);
		
		RedisModel model = redisServerService.get("4", RedisModel.class);
		System.out.println(model.getUsername());
		System.out.println(model.getPassword());
		
		Set<String> keys = new HashSet<String>();
		keys.add("3");
		keys.add("4");
		
		List<RedisModel> models = redisServerService.multiGet(keys, RedisModel.class);
		for(RedisModel model1 : models) {
			System.out.println(model1.getUsername());
			System.out.println(model1.getPassword());
		}	
	}
}
