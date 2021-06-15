package com.example.fileprocessor.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.fileprocessor.model.TextFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Adobe Systems Incorporated.
 */
public interface TextFileService {
  Logger logger = LoggerFactory.getLogger(TextFileService.class);

  default Optional<TextFile> getTextFile(String textFile, ObjectMapper objectMapper) {
    Optional<TextFile> deserializedTextFile;

    try {
      deserializedTextFile = Optional.of(objectMapper.readValue(textFile, TextFile.class));
    } catch (JsonProcessingException e) {
      logger.info("Unable to deserialize text file object: {}. Exception: {}", textFile, e.toString());
      deserializedTextFile = Optional.empty();
    }
    return deserializedTextFile;
  }
}
