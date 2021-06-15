package com.example.fileprocessor.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Adobe Systems Incorporated.
 */
@Configuration
public class JedisPoolFactory {
  private final String host;
  private final Integer port;
  private final int timeout;
  private final int maxActive;
  private final int maxIdle;
  private final int minIdle;
  private final long maxWaitMills;

  @Autowired
  public JedisPoolFactory(
    @Value("${redis.host:127.0.0.1}") String host,
    @Value("${redis.port:6379}") int port,
    @Value("${redis.timeout:10}") int timeout,
    @Value("${redis.max-active:200}") int maxActive,
    @Value("${redis.max-idle:10}") int maxIdle,
    @Value("${redis.min-idle:3}") int minIdle,
    @Value("${redis.max-wait-mills:10000}") long maxWaitMills) {
    this.host = host;
    this.port = port;
    this.timeout = timeout;
    this.maxActive = maxActive;
    this.maxIdle = maxIdle;
    this.minIdle = minIdle;
    this.maxWaitMills = maxWaitMills;
  }

  @Bean
  public JedisPool generateJedisPool() {
    final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    jedisPoolConfig.setMaxTotal(maxActive);
    jedisPoolConfig.setMaxIdle(maxIdle);
    jedisPoolConfig.setMinIdle(minIdle);
    jedisPoolConfig.setMaxWaitMillis(maxWaitMills);
    jedisPoolConfig.setBlockWhenExhausted(Boolean.TRUE);

    return new JedisPool(jedisPoolConfig, host, port, timeout);
  }
}
