package com.aries.demo.dynamic.redis.configs;

import com.aries.demo.dynamic.constant.Constant;
import com.lenovo.id.redis.service.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态设置路由，用于AOP的设置
 */
@Component
public class DynamicRedisClientRouting extends AbstractRoutingRedisSource {

    @Autowired
    private RedisClient redisClient;

    @PostConstruct
    public void init() {
        RedisClient reDisClientSource = new DynamicRedisSource(redisClient).getReDisClientSource();
        super.setDefaultTargetRedisSource(reDisClientSource);
        Map<Object, RedisClient> targetRedisSources = new ConcurrentHashMap<>();
        targetRedisSources.put(Constant.REDISSOURCE_TAG,reDisClientSource);
        super.setTargetRedisSources(targetRedisSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String lookupKey = RedisClientSourceContextHolder.getDataSource();
        return lookupKey;
    }
}
