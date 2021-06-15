package com.example.fileprocessor.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fileprocessor.model.TextFile;
import com.example.fileprocessor.repository.TextFileRepository;
import com.example.fileprocessor.util.RedisDataGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.example.fileprocessor.util.RedisDataGenerator.buildHashKey;
import static com.example.fileprocessor.util.RedisDataGenerator.buildIpSetKey;
import static com.example.fileprocessor.util.RedisDataGenerator.buildTextFileFromHashValue;
import static com.example.fileprocessor.util.RedisDataGenerator.getTaskIdsQueueName;

/**
 * @author Adobe Systems Incorporated.
 */
@Service
public class TextFileOperationService implements TextFileService{
  private final Logger logger = LoggerFactory.getLogger(TextFileOperationService.class);

  private final ObjectMapper objectMapper;
  private final TextFileRepository textFileRepository;

  @Autowired
  public TextFileOperationService(
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

  public Optional<List<TextFile>> getFiles(String ipAddress) {
    final Set<String> filesByIp = textFileRepository.getFilesByIp(buildIpSetKey(ipAddress), 0, -1);

    if (!filesByIp.isEmpty()) {
      final List<TextFile> textFiles = filesByIp.stream()
        .map(RedisDataGenerator::buildHashKey)
        .map(textFileRepository::getFile)
        .map(RedisDataGenerator::buildTextFileFromHashValue)
        .collect(Collectors.toList());
      return Optional.of(textFiles);
    } else {
      return Optional.empty();
    }
  }

}
