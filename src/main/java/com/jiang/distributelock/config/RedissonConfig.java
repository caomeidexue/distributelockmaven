package com.jiang.distributelock.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 〈功能概述〉<br>
 *
 * @author: yiche
 * @date: 2021/3/9 3:36 下午
 */
@Configuration
public class RedissonConfig {

    @Value("${redisIp}")
    private String redisIp;
    @Value("${port}")
    private String port;

    @Bean
    public RedissonClient getClient(){
        Config config = new Config();
        String addr = "redis://" + redisIp+":"+port;
        config.useSingleServer().setAddress(addr);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
