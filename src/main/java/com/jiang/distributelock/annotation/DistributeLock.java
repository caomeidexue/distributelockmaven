package com.jiang.distributelock.annotation;

import org.springframework.lang.NonNull;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author jiangqb
 * 分布式锁注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface DistributeLock {

    /**
     *分布式锁key
     */
    @NonNull
    public String lockKey();

    /**
     * 锁定时间
     * @return
     */
    public long expireTime();

    /**
     * 锁定时间单位
     * @return
     */
    public TimeUnit timeUnit();

}
