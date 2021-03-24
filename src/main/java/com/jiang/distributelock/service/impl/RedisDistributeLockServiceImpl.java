package com.jiang.distributelock.service.impl;

import com.jiang.distributelock.service.DistributeLockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 〈功能概述〉<br>
 *
 * @author: yiche
 * @date: 2021/3/9 3:08 下午
 */
@Service
public class RedisDistributeLockServiceImpl implements DistributeLockService {


    @Autowired
    @Qualifier("getClient")
    private RedissonClient redissonClient;

    @Override
    public boolean tryLock(String lockKey, long expireTime, TimeUnit timeUnit) throws Exception {

        RLock lock = redissonClient.getLock(lockKey);
        try{
            boolean res = lock.tryLock(0, expireTime, timeUnit);
            return res;
        } catch (Exception e) {
           throw e;
        }
    }

    @Override
    public boolean releaseLock(String lockKey) throws Exception {
        RLock lock = redissonClient.getLock(lockKey);
        lock.unlock();
        return true;
    }
}
