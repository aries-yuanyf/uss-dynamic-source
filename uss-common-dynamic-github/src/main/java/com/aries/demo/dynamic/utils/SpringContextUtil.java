package com.aries.demo.dynamic.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 获取spring中的bean对象工具类
 * @author aries-yuanyf
 * @date 2021-03-29
 */
@Component
public class SpringContextUtil implements ApplicationContextAware, BeanPostProcessor{
    /**
     *  Spring应用上下文环境
     */
    private static ApplicationContext applicationContext;
    @Autowired
    private static DefaultListableBeanFactory defaultListableBeanFactory;

    /**
     * 实现ApplicationContextAware接口的回调方法，设置上下文环境
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取对象 这里重写了bean方法，起主要作用
     */
    public static Object getBean(String beanId){

        try{
            return applicationContext.getBean(beanId);
        }catch (NoSuchBeanDefinitionException e){
            return null;
        }


    }
    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
        try{
            return getApplicationContext().getBean(clazz);
        }catch (NoSuchBeanDefinitionException e){
            return null;
        }

    }

    /**
     * 是否存在该Class bean
     * @param clazz
     * @return
     */
    public static Boolean exist(Class clazz){
        Object bean = getBean(clazz);
        if(bean != null){
            return true;
        }
        return false;
    }

    //通过class获取Bean.
    public static String[] getBeanNames(){
        return getApplicationContext().getBeanDefinitionNames();
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name,Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
    public static void setBean(String beanName,Class<?> beanClass) throws BeansException {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        // 获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        defaultListableBeanFactory.removeBeanDefinition(beanName);
        defaultListableBeanFactory.registerBeanDefinition(beanName, BeanDefinitionBuilder.genericBeanDefinition(beanClass).getBeanDefinition());
    }

    public static void setBean(Class<?> beanClass) throws BeansException {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        // 获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        String[] beanNames = getApplicationContext().getBeanNamesForType(beanClass);
        if(beanNames.length>0){
            for(int i=0;i<beanNames.length;i++){
                defaultListableBeanFactory.removeBeanDefinition(beanNames[i]);
                defaultListableBeanFactory.registerBeanDefinition(beanNames[i], BeanDefinitionBuilder.genericBeanDefinition(beanClass).getBeanDefinition());
            }
        }

    }

}
