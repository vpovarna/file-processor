package com.example.fileprocessor.service;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.fileprocessor.model.TextFile;
import com.example.fileprocessor.repository.TextFileRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.example.fileprocessor.util.RedisHMapGenerator.buildHashKey;
import static com.example.fileprocessor.util.RedisHMapGenerator.buildHashValue;
import static com.example.fileprocessor.util.RedisHMapGenerator.buildTextFileFromHashValue;
import static com.example.fileprocessor.util.RedisHMapGenerator.getTaskIdsQueueName;

/**
 * @author Adobe Systems Incorporated.
 */
@Service
public class TextFileService {
  private final Logger logger = LoggerFactory.getLogger(TextFileService.class);

  private final ObjectMapper objectMapper;
  private final TextFileRepository textFileRepository;

  @Autowired
  public TextFileService(
    ObjectMapper objectMapper,
    TextFileRepository textFileRepository) {
    this.objectMapper = objectMapper;
    this.textFileRepository = textFileRepository;
  }

  public Long addFileToQueue(TextFile textFile) {
    Optional<String> serializedTextFile;
    try {
      serializedTextFile = Optional.of(objectMapper.writeValueAsString(textFile));
    } catch (JsonProcessingException e) {
      logger.info("Unable to serialize text file object: {}. Exception: {}", textFile, e.toString());
      serializedTextFile = Optional.empty();
    }

    if (serializedTextFile.isPresent()) {
      return textFileRepository.addTaskIdToList(getTaskIdsQueueName(), serializedTextFile.get());
    } else {
      return -1L;
    }
  }

  public Optional<TextFile> getFile(String  taskId) {
    final String keyName = buildHashKey(taskId);
    final Map<String, String> textFileHash = textFileRepository.getFile(keyName);
    if (textFileHash.isEmpty()) {
      logger.info("No file found in the repository with keyName: {}", keyName);
      return Optional.empty();
    }
    return Optional.of(buildTextFileFromHashValue(textFileHash));
  }

  @Scheduled(fixedDelayString = "${queue.process.delay}")
  public void start() {
    logger.info("Start reading the queue for any available tasks!");
    final String queueId = getTaskIdsQueueName();
    final Long queueSize = textFileRepository.getQueueSize(queueId);

    if (queueSize <= 0) {
      logger.info("No elements in the queue for processing!");
      return;
    }

    final String textFile = textFileRepository.getFirstInsertedTask(queueId);

    Optional<TextFile> deserializedTextFile;

    try {
      deserializedTextFile = Optional.of(objectMapper.readValue(textFile, TextFile.class));
    } catch (JsonProcessingException e) {
      logger.info("Unable to deserialize text file object: {}. Exception: {}", textFile, e.toString());
      deserializedTextFile = Optional.empty();
    }

    if (deserializedTextFile.isPresent()) {
      final TextFile file = deserializedTextFile.get();
      final String fileKeyId = buildHashKey(file.getTaskId());
      final Map<String, String> fileValue = buildHashValue(file);

      logger.info("Writing file, with id: {} value: {}", fileKeyId, fileValue);
      textFileRepository.addFile(fileKeyId, fileValue);
    }
  }
}
