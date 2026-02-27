package com.leipeng.redis.server.service;

public interface LockService {
    boolean lock(String key, String value);
    void unlock(String key);
}
