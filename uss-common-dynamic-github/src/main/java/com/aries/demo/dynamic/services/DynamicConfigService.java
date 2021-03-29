package com.aries.demo.dynamic.services;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


/**
 *
 * apollo 动态config server
 * 该类是获取apollo 配置信息的公共类,继承该类则子类可以正常使用apollo的特性
 * @author:aries-yuanyf
 * @date:2021-03-29
 *
 */
@Configuration
@EnableApolloConfig({"application"})
@Component
public  class DynamicConfigService {


    @ApolloConfig(value = "application")
    private Config applicationConfig;

    /**
     * 获取String类型的配置信息
     * @param key 配置的key
     * @param defaultValue 找不到改配置时的默认值
     * @return
     */
    public  String getValueByKey(String key,String defaultValue){
        return  applicationConfig.getProperty(key, defaultValue);
    }


    /**
     * 获取Integer类型的配置信息
     * @param key 配置的key
     * @param defaultValue 找不到改配置时的默认值
     * @return
     */
    public  Integer getIntValueByKey(String key,Integer defaultValue){
        return applicationConfig.getIntProperty(key, defaultValue);
    }

    /**
     * 获取String类型的配置信息,默认值为""
     * @param key
     * @return
     */
    public  String getValueByKey(String key){
        return getValueByKey(key,"");
    }



}
