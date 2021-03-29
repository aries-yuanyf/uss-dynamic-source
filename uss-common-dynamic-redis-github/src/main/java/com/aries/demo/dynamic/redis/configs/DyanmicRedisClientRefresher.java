package com.aries.demo.dynamic.redis.configs;

import com.aries.demo.dynamic.services.DynamicConfigService;
import com.aries.demo.dynamic.utils.SpringContextUtil;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.lenovo.id.dynamic.redis.configs.DynamicManager;
import com.lenovo.id.dynamic.redis.configs.DynamicRedisSource;
import com.lenovo.id.redis.service.RedisClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.util.Set;


/**
 * redis 动态监听apollo redis配置项目,一点配置项发生改变则重新实例化redis客户端
 * 该类继承了uss-common-dynamic-github工程的DynamicConfigService。可以自动使用apollo的方法
 */
@Component
public  class DyanmicRedisClientRefresher extends DynamicConfigService {

    protected final Log log = LogFactory.getLog(DyanmicRedisClientRefresher.class);

    /**
     * 动态监听变化
     * @param changeEvent
     */
    @ApolloConfigChangeListener(value = {"application"})
    public synchronized void onChange(ConfigChangeEvent changeEvent){
        Set<String> changedKeys = changeEvent.changedKeys();
        for(String changekey:changedKeys){
            if(changekey.contains("redis.")){

                try{
                    log.info("redisclient instance reinit....start");
                    //获取新的RedisClient 实例
                    DynamicManager dynamicManager = SpringContextUtil.getBean(DynamicManager.class);
                    DynamicRedisSource dynamicRedisSource = SpringContextUtil.getBean(DynamicRedisSource.class);
                    if(dynamicManager != null){
                        RedisClient newRedisClient = dynamicManager.createRedisClient();
                        dynamicRedisSource.setRedisClientSource(newRedisClient);
                    }
                    log.info("redisclient instance reinit....finished ");
                }catch (Exception e){
                    e.printStackTrace();
                    log.info("redisclient instance reinit....exception");
                }

            }
        }

    }

}
