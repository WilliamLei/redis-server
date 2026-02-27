package com.leipeng.redis.server.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LockServiceTest {

    @InjectMocks
    private LockServiceImpl lockService;

    @Mock
    private NewRedisService<String, String> newRedisService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLock_Success() {
        when(newRedisService.setNx("testKey", "testValue")).thenReturn(true);
        boolean result = lockService.lock("testKey", "testValue");
        assertTrue(result);
        verify(newRedisService, times(1)).setNx("testKey", "testValue");
    }

    @Test
    public void testLock_Failure() {
        when(newRedisService.setNx("testKey", "testValue")).thenReturn(false);
        boolean result = lockService.lock("testKey", "testValue");
        assertFalse(result);
        verify(newRedisService, times(1)).setNx("testKey", "testValue");
    }

    @Test
    public void testUnlock() {
        doNothing().when(newRedisService).delete("testKey");
        lockService.unlock("testKey");
        verify(newRedisService, times(1)).delete("testKey");
    }
}
