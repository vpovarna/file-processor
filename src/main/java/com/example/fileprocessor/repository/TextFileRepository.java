package com.example.fileprocessor.repository;

import java.util.Map;
import java.util.Set;

/**
 * @author Adobe Systems Incorporated.
 */
public interface TextFileRepository {

  Long addFile(String keyName, Map<String, String> value);

  Map<String, String> getFile(String keyName);

  Boolean fileExist(String keyName);

  Long addFileIdToIp(String key, double score, String member);

  Set<String> getFilesByIp(String key, long startIndex, long endIndex);

  Long addTaskIdToList(String key, String value);

  String getFirstInsertedTask(String key);

  Long getQueueSize(String key);
}
