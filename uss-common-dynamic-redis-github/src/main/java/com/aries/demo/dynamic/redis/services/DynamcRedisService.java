package com.aries.demo.dynamic.redis.services;

import com.aries.demo.dynamic.constant.Constant;
import com.aries.demo.dynamic.redis.configs.DynamicRedisClientRouting;
import com.aries.demo.dynamic.redis.configs.RedisClientSourceContextHolder;
import com.lenovo.id.redis.service.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * redis 对外提供的server,
 * dynamcRedisService.determineCurrentRedis()可以获取到动态redis客户端
 */
@Service
public class DynamcRedisService {

    @Autowired
    private DynamicRedisClientRouting dynamicRedisClientRouting;
    public RedisClient determineCurrentRedis(){
        RedisClientSourceContextHolder.putDataSource(Constant.REDISSOURCE_TAG);
        return dynamicRedisClientRouting.determineTargetRedisSource();
    }


}
