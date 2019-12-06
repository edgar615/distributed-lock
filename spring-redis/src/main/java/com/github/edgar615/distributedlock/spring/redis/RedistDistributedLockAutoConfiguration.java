package com.github.edgar615.distributedlock.spring.redis;

import com.github.edgar615.distributedlock.DistributedLockProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
//@ConditionalOnBean(RedisConnectionFactory.class)
public class RedistDistributedLockAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public DistributedLockProvider distributedLockProvider(
      RedisTemplate<String, String> redisTemplate) {
    return new SimpleRedistDistributedLockProvider(redisTemplate);
  }
}
