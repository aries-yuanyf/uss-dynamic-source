package com.aries.demo.dynamic.redis.configs;



/***
 * 动态数据源持有者，负责利用ThreadLocal存取数据源名称
 * 标记不同数据源 (将不同的数据源标识记录在ThreadLocal中)
 */
public class RedisClientSourceContextHolder {

    /**
     * 本地线程共享对象
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER_THREAD_LOCAL = new ThreadLocal<>();

    ThreadLocal<String> getDataSouceThreadLocal() {
        return CONTEXT_HOLDER_THREAD_LOCAL;
    }

    /**
     * 提供给AOP去设置当前的线程的数据源的信息　
     * @param datasource
     */
    public static void putDataSource(String datasource) {
        CONTEXT_HOLDER_THREAD_LOCAL.set(datasource);
    }

    /**
     *  提供给AbstractRoutingDataSource的实现类，通过key选择数据源
     * @return
     */
    public static String getDataSource() {
        return CONTEXT_HOLDER_THREAD_LOCAL.get();
    }

    /**
     *
     */
    public static void clear() {
        CONTEXT_HOLDER_THREAD_LOCAL.remove();
    }

}