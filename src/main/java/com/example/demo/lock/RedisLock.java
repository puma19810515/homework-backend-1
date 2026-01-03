package com.example.demo.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
public class RedisLock {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 嘗試取得分布式鎖
     */
    public boolean tryLock(String key, String value, long second) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, second, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }


    private static final String LUA_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "   return redis.call('del', KEYS[1]) " +
                    "else " +
                    "   return 0 " +
                    "end";
    /**
     * 釋放分布式鎖
     */
    public  boolean releaseLock(String key, String value) {
        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(LUA_SCRIPT, Long.class),
                Collections.singletonList(key),
                value
        );
        return Long.valueOf(1).equals(result);

    }

}
