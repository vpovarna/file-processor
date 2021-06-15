package com.example.fileprocessor.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.JedisPool;

/**
 * @author Adobe Systems Incorporated.
 */
@Repository
public class TextFileRepositoryImp implements TextFileRepository{

  private final JedisPool jedisPool;

  @Autowired
  public TextFileRepositoryImp(JedisPool jedisPool) {
    this.jedisPool = jedisPool;
  }

  @Override
  public Long addFile(String keyName, String value) {
    return null;
  }

  @Override
  public String getFile(String keyName) {
    return null;
  }

  @Override
  public int fileExist(String keyName) {
    return 0;
  }

  @Override
  public Long addFileIdToIp(String key, double score, String member) {
    return null;
  }

  @Override
  public List<String> getFilesByIp(String key, long startIndex, long endIndex) {
    return null;
  }
}
