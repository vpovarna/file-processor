package com.example.fileprocessor.repository;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author Adobe Systems Incorporated.
 */
@Repository
public class TextFileRepositoryImp implements TextFileRepository{
  private final Logger logger = LoggerFactory.getLogger(TextFileRepositoryImp.class);

  private final JedisPool jedisPool;

  @Autowired
  public TextFileRepositoryImp(JedisPool jedisPool) {
    this.jedisPool = jedisPool;
  }

  @Override
  public Long addFile(String keyName, Map<String, String> value) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.hset(keyName, value);
    } catch (Exception e) {
      logger.error("Unable to add file into the repository: {}", e.toString());
      return -1L;
    }
  }

  @Override
  public Map<String, String> getFile(String keyName) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.hgetAll(keyName);
    } catch (Exception e) {
      logger.error("Unable to fetch key mao values for the key: {}. Exception: {}", keyName, e.toString());
      return Collections.emptyMap();
    }
  }

  @Override
  public Boolean fileExist(String keyName) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.exists(keyName);
    } catch (Exception e) {
      logger.error("Error trying to find key: {} into repository. Exception: {}", keyName, e.toString());
      return Boolean.FALSE;
    }
  }

  @Override
  public Long addFileIdToIp(String keyName, double score, String member) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.zadd(keyName, score, member);
    } catch (Exception e) {
      logger.error("Error updating key: {}, into repository. Exception: {}", keyName, e.toString());
      return -1L;
    }
  }

  @Override
  public Set<String> getFilesByIp(String keyName, long startIndex, long endIndex) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.zrange(keyName, startIndex, startIndex);
    } catch (Exception e) {
      logger.error("Error fetching key: {} values. Exception: {}", keyName, e.toString());
      return Collections.emptySet();
    }
  }
}
