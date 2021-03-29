package com.aries.demo.dynamic.redis.configs;

import com.lenovo.id.redis.service.RedisClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;
@Component
public class DynamicRedisSource {

    private final AtomicReference<RedisClient> redisClientAtomicReference;

    public DynamicRedisSource(RedisClient redisClient) {
        this.redisClientAtomicReference = new AtomicReference<>(redisClient);
    }

    public RedisClient setRedisClientSource(RedisClient redisClient){
        return redisClientAtomicReference.getAndSet(redisClient);
    }
    public RedisClient getReDisClientSource(){
        return redisClientAtomicReference.get();
    }
}
