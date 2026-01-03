package com.example.demo.lock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisLockTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisLock redisLock;

    private String lockKey = "testLockKey";
    private String lockValue = "testLockValue";

    @Test
    void tryLock_success(){
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(
                lockKey, lockValue, 10, TimeUnit.SECONDS)
        ).thenReturn(true);

        boolean result = redisLock.tryLock(lockKey, lockValue, 10);

        assertTrue(result);
    }

    @Test
    void tryLock_failure() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(
                lockKey, lockValue, 10, TimeUnit.SECONDS)
        ).thenReturn(false);

        boolean result = redisLock.tryLock(lockKey, lockValue, 10);

        assertFalse(result);
    }

    @Test
    void releaseLock_success_when_lua_return_1() {
        when(stringRedisTemplate.execute(
                any(DefaultRedisScript.class),
                eq(Collections.singletonList(lockKey)),
                eq(lockValue)
        )).thenReturn(1L);

        boolean result = redisLock.releaseLock(lockKey, lockValue);

        assertTrue(result);
    }

    @Test
    void releaseLock_fail_when_lua_return_0() {
        when(stringRedisTemplate.execute(
                any(DefaultRedisScript.class),
                eq(Collections.singletonList(lockKey)),
                eq(lockValue)
        )).thenReturn(0L);

        boolean result = redisLock.releaseLock(lockKey, lockValue);

        assertFalse(result);
    }

    @Test
    void releaseLock_fail_when_lua_return_null() {
        // given
        when(stringRedisTemplate.execute(
                any(DefaultRedisScript.class),
                eq(Collections.singletonList(lockKey)),
                eq(lockValue)
        )).thenReturn(null);

        // when
        boolean result = redisLock.releaseLock(lockKey, lockValue);

        // then
        assertFalse(result);
    }
}
