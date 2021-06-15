package com.example.fileprocessor.repository;

import java.util.List;

/**
 * @author Adobe Systems Incorporated.
 */
public interface TextFileRepository {

  Long addFile(String keyName, String value);

  String getFile(String keyName);

  int fileExist(String keyName);

  Long addFileIdToIp(String key, double score, String member);

  List<String> getFilesByIp(String key, long startIndex, long endIndex);

}
