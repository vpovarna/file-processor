package com.example.fileprocessor.service;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.fileprocessor.model.TextFile;
import com.example.fileprocessor.repository.TextFileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.example.fileprocessor.util.RedisDataGenerator.getTaskIdsQueueName;

/**
 * @author Adobe Systems Incorporated.
 */
@Service
public class TextFileQueueService implements TextFileService {
  private final Logger logger = LoggerFactory.getLogger(TextFileQueueService.class);

  private final TextFileRepository textFileRepository;
  private final ObjectMapper objectMapper;
  private final ThreadPoolExecutor executorService;

  @Autowired
  public TextFileQueueService(
    @Value("${threadPool.size:2}") int size,
    TextFileRepository textFileRepository,
    ObjectMapper objectMapper) {
    this.textFileRepository = textFileRepository;
    this.objectMapper = objectMapper;
    this.executorService = new ThreadPoolExecutor(
      size, size, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(size));
  }

  @Scheduled(fixedDelayString = "${queue.process.delay}")
  public void start() {
    logger.debug("Start reading the queue for any available tasks!");
    final String queueId = getTaskIdsQueueName();
    final Long queueSize = textFileRepository.getQueueSize(queueId);

    if (queueSize <= 0) {
      logger.debug("No elements in the queue for processing!");
      return;
    }

    final String serializedTextFile = textFileRepository.getFirstInsertedTask(queueId);
    final Optional<TextFile> deserializedTextFile = getTextFile(serializedTextFile, objectMapper);

    if (deserializedTextFile.isPresent()) {
      final ItemTask itemTask = new ItemTask(deserializedTextFile.get(), textFileRepository);
      executorService.execute(itemTask);
    }
  }

}
