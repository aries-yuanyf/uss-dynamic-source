package com.aries.demo.dynamic.redis.configs;

import com.aries.demo.dynamic.services.DynamicConfigService;
import com.lenovo.id.redis.service.RedisClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * redis数据源实例化管理类，用户初次实例及动态实例化redis
 *
 */
@Component
public class DynamicManager {
    private static final Logger log = Logger.getLogger(DynamicManager.class);
    @Autowired
    private DynamicConfigService dynamicConfigService;
    @Autowired
    private RedisClient redisClient;


    /**
     * 实例化 Redis客户端
     * @return
     */
    public RedisClient createRedisClient(){
        if(redisClient == null){
             redisClient = new RedisClient();
        }
        //----------------------------获取redis的properties属性----------------------------------
        int timeout = dynamicConfigService.getIntValueByKey("redis.cluster.timeout",null);
        int maxRedirections = dynamicConfigService.getIntValueByKey("redis.cluster.maxredirections",null);
        String  isCluster = dynamicConfigService.getValueByKey("isCluster");
        String  maxTotal = dynamicConfigService.getValueByKey("redis.maxTotal");
        String maxIdle = dynamicConfigService.getValueByKey("redis.maxIdle");
        String clusterList = dynamicConfigService.getValueByKey("redis.cluster.list");
        String maxWaitTime = dynamicConfigService.getValueByKey("redis.maxWaitTime");
        String maxIdleClu = dynamicConfigService.getValueByKey("redis.cluster.maxIdle");
        String host = dynamicConfigService.getValueByKey("redis.host");
        String port = dynamicConfigService.getValueByKey("redis.port");

        redisClient.setMaxIdle(maxIdle);
        redisClient.setMaxWaitTime(maxWaitTime);
        redisClient.setMaxTotal(maxTotal);
        redisClient.setTimeout(timeout);
        redisClient.setMaxRedirections(maxRedirections);

        redisClient.setClusterList(clusterList);
        redisClient.setMaxIdleClu(maxIdleClu);

        if (Boolean.valueOf(isCluster)) {
            //----------------------------集群实例化----------------------------------
            log.info("### redisCluster re-init start ... " + isCluster + " isRedisCluster:" + redisClient.isRedisCluster());
            GenericObjectPoolConfig genericObjectPoolConfig =  new GenericObjectPoolConfig();
            genericObjectPoolConfig.setMaxWaitMillis(-1L);
            genericObjectPoolConfig.setMaxTotal(Integer.parseInt(maxTotal));
            genericObjectPoolConfig.setMaxIdle(Integer.parseInt(maxIdle));
            Set<HostAndPort> haps = this.parseHostAndPort(clusterList);
            JedisCluster jedisCluster = new JedisCluster(haps, timeout, maxRedirections, genericObjectPoolConfig);
            redisClient.setJedisCluster(jedisCluster);//让setJedisCluster重新加载
            log.info("### redisCluster re-init end ... current idc:" + isCluster + " isRedisCluster:" + redisClient.isRedisCluster());
        }else{
            //----------------------------jdis实例化----------------------------------
            log.info("### redis re-init start ... " + isCluster + " isRedisCluster:" + redisClient.isRedisCluster());
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxIdle(Integer.parseInt(maxIdle));
            config.setMaxTotal(Integer.parseInt(maxTotal));
            config.setSoftMinEvictableIdleTimeMillis(1800000L);
            config.setMaxWaitMillis(Long.parseLong(maxWaitTime));
            config.setTestOnBorrow(true);
            JedisPool pool = new JedisPool(config, host, Integer.parseInt(port), 100000);
            redisClient.setPool(pool);
            log.info("### redis re-init end ... " + isCluster + " isRedisCluster:" + redisClient.isRedisCluster());
        }
        return redisClient;
    }
    public Set<HostAndPort> parseHostAndPort(String clusterList)  {
        try {
            Set<HostAndPort> haps = new HashSet();
            String[] clusterListArray = clusterList.split(",");
            String[] var3 = clusterListArray;
            int var4 = clusterListArray.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String hostport = var3[var5];
                String[] ipAndPort = hostport.split(":");
                HostAndPort hap = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
                haps.add(hap);
            }

            return haps;
        } catch (IllegalArgumentException var9) {
            var9.printStackTrace();
            throw var9;
        } catch (Exception var10) {
            var10.printStackTrace();
            return null;
        }
    }

}
