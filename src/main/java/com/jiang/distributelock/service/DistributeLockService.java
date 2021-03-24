package com.jiang.distributelock.service;

import java.util.concurrent.TimeUnit;

/**
 * 〈功能概述〉<br>
 *
 * @author: yiche
 * @date: 2021/3/9 3:07 下午
 */
public interface DistributeLockService {

    /**
     * 锁
     * @param lockKey
     * @param expireTime
     * @param timeUnit
     * @return
     * @throws Exception
     */
    boolean tryLock(String lockKey, long expireTime, TimeUnit timeUnit) throws Exception;

    /**
     * 释放锁
     * @param lockKey
     * @return
     * @throws Exception
     */
    boolean releaseLock(String lockKey) throws Exception ;

}
