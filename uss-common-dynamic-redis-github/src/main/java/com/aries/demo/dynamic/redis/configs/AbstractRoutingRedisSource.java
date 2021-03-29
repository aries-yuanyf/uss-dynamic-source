package com.aries.demo.dynamic.redis.configs;

import com.lenovo.id.redis.service.RedisClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 针对Redis数据源重构的AbstractRoutingRedisSource
 * 主要方法
 * @see #afterPropertiesSet
 * @see #determineTargetRedisSource
 * @see #determineCurrentLookupKey
 * @author aries-yuanyf
 * @date 2021-03-29
 *
 */
public abstract class AbstractRoutingRedisSource   implements InitializingBean {


    @Nullable
    public Map<Object, RedisClient> targetRedisSources;
    public RedisClient defaultTargetRedisSource;
    @Nullable
    public Map<Object, RedisClient> resolvedRedisSources;
    public boolean lenientFallback = true;
    @Nullable
    public RedisClient resolvedDefaultRedisSource;

    @Nullable
    public Map<Object, RedisClient> getTargetRedisSources() {
        return targetRedisSources;
    }

    /**
     *设置目标Redis数据源
     * @param targetRedisSources
     */
    public void setTargetRedisSources(@Nullable Map<Object, RedisClient> targetRedisSources) {
        this.targetRedisSources = targetRedisSources;
    }

    public Object getDefaultTargetRedisSource() {
        return defaultTargetRedisSource;
    }

    /**
     * 设置默认数据源
     * @param defaultTargetRedisSource
     */
    public void setDefaultTargetRedisSource(RedisClient defaultTargetRedisSource) {
        this.defaultTargetRedisSource = defaultTargetRedisSource;
    }

    @Nullable
    public Map<Object, RedisClient> getResolvedRedisSources() {
        return resolvedRedisSources;
    }

    public void setResolvedRedisSources(@Nullable Map<Object, RedisClient> resolvedRedisSources) {
        this.resolvedRedisSources = resolvedRedisSources;
    }

    public boolean isLenientFallback() {
        return lenientFallback;
    }

    public void setLenientFallback(boolean lenientFallback) {
        this.lenientFallback = lenientFallback;
    }

    @Nullable
    public RedisClient getResolvedDefaultRedisSource() {
        return resolvedDefaultRedisSource;
    }

    public void setResolvedDefaultRedisSource(@Nullable RedisClient resolvedDefaultRedisSource) {
        this.resolvedDefaultRedisSource = resolvedDefaultRedisSource;
    }


    /**
     * 实例化数据
     */
    public void afterPropertiesSet() {
        if (this.targetRedisSources == null) {
            throw new IllegalArgumentException("Property 'targetRedisSources' is required");
        } else {
            this.resolvedRedisSources = new HashMap(this.targetRedisSources.size());
            if(this.targetRedisSources.size()>0){
                for (Map.Entry<Object, RedisClient> entry : this.targetRedisSources.entrySet()) {
                    Object lookupKey = this.resolveSpecifiedLookupKey(entry.getKey());
                    RedisClient redisClient = this.resolveSpecifiedRedisSource(entry.getValue());
                    this.resolvedRedisSources.put(lookupKey, redisClient);
                }
            }
            if (this.defaultTargetRedisSource != null) {
                this.resolvedDefaultRedisSource = this.resolveSpecifiedRedisSource(this.defaultTargetRedisSource);
            }

        }
    }


    /**
     *  获取当前key的数据源
     * @return
     */
    public RedisClient determineTargetRedisSource() {
        Assert.notNull(this.resolvedRedisSources, "RedisClient router not initialized");
        Object lookupKey = determineCurrentLookupKey();//这里获取数据库标识
        RedisClient redisSource = this.resolvedRedisSources.get(lookupKey);//获得具体的数据源
        if (redisSource == null && (this.lenientFallback || lookupKey == null)) {
            redisSource = this.resolvedDefaultRedisSource;
        }
        if (redisSource == null) {
            throw new IllegalStateException("Cannot determine target redisSource for lookup key [" + lookupKey + "]");
        }
        return redisSource;
    }

    /**
     * 获取当前key
     * @return
     */
    @Nullable
    protected abstract Object determineCurrentLookupKey();
    protected Object resolveSpecifiedLookupKey(Object lookupKey) {
        return lookupKey;
    }


    protected RedisClient resolveSpecifiedRedisSource(Object redisSource) throws IllegalArgumentException {
        if (redisSource instanceof RedisClient) {
            return (RedisClient)redisSource;
        } else {
            throw new IllegalArgumentException("Illegal data source value - only [javax.sql.DataSource] and String supported: " + redisSource);
        }
    }


}
