package com.example.fileprocessor.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.fileprocessor.model.TextFile;
import com.example.fileprocessor.repository.TextFileRepository;
import com.example.fileprocessor.util.RedisDataGenerator;

import static com.example.fileprocessor.util.RedisDataGenerator.buildHashKey;
import static com.example.fileprocessor.util.RedisDataGenerator.buildHashValue;

/**
 * @author Adobe Systems Incorporated.
 */

public class ItemTask implements Runnable{
  private final Logger logger = LoggerFactory.getLogger(ItemTask.class);

  private final TextFile file;
  private final TextFileRepository textFileRepository;

  public ItemTask(TextFile textFile, TextFileRepository textFileRepository) {
    this.file = textFile;
    this.textFileRepository = textFileRepository;
  }

  @Override
  public void run() {
    //TODO: Might require a redis transaction
    addFileIdToIpSet(file.getTaskId(), file.getIps(), file.getTaskCreationDate());

    final String fileKeyId = buildHashKey(file.getTaskId());
    final Map<String, String> fileValue = buildHashValue(file);

    logger.info("Writing file, with id: {} value: {}", fileKeyId, fileValue);
    textFileRepository.addFile(fileKeyId, fileValue);
  }

  private void addFileIdToIpSet(String taskId, List<String> ips, LocalDate taskCreationDate) {
    logger.info("Adding taskId: {} to IP set: {}", taskId, ips);

    //TODO: Maybe we need millis in UTC
    final double createdAt = (double) taskCreationDate.toEpochDay();
    ips.stream()
      .map(RedisDataGenerator::buildIpSetKey)
      .forEach(key -> textFileRepository.addFileIdToIp(key, createdAt, taskId));
  }
}
