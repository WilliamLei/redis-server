package com.leipeng.redis.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LockServiceImpl implements LockService {

    @Autowired
    private NewRedisService<String, String> newRedisService;

    @Override
    public boolean lock(String key, String value) {
        return newRedisService.setNx(key, value);
    }

    @Override
    public void unlock(String key) {
        newRedisService.delete(key);
    }
}
