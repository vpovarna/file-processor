package com.example.fileprocessor.util;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.example.fileprocessor.model.TextFile;

/**
 * @author Adobe Systems Incorporated.
 */
public class RedisDataGenerator {

  private static final String STATUS_CODE = "StatusCode";
  private static final String CREATED_AT = "CreatedAt";
  private static final String IPS = "IPs";
  private static final String TASK_ID = "TaskId";
  private static final String FILE_ID = "FileId";
  private static final String FILE = "FILE";
  private static final String SEPARATOR = ":";
  private static final String TEXT_FILES = "TEXT_FILES";
  private static final String QUEUE_NAME = "QUEUE";
  private static final String LIST_SEPARATOR = ",";
  private static final String LIST = "LIST";
  private static final String IP = "IP";

  private RedisDataGenerator() {
  }

  public static Map<String, String> buildHashValue(TextFile textFile) {
    final HashMap<String, String> map = new HashMap<>();

    map.put(FILE_ID, textFile.getFileId());
    map.put(TASK_ID, textFile.getTaskId());
    map.put(IPS, textFile.getIps().toString());
    map.put(CREATED_AT, String.valueOf(textFile.getTaskCreationDate()));
    map.put(STATUS_CODE, textFile.getTaskStatus().getValue());

    return map;
  }

  public static TextFile buildTextFileFromHashValue(Map<String, String> map) {
    final TextFile textFile = new TextFile();

    textFile.setFileId(map.get(FILE_ID));
    textFile.setTaskId(map.get(TASK_ID));

    final String ips = map.get(IPS);
    final String[] splitIps = ips.split(LIST_SEPARATOR);
    if (splitIps.length > 0) {
      textFile.setIps(Arrays.asList(splitIps));
    }

    final String statusCode = map.get(STATUS_CODE);
    textFile.setTaskStatus(TextFile.TaskStatusEnum.fromValue(statusCode));

    final String createdAt = map.get(CREATED_AT);
    textFile.setTaskCreationDate(LocalDate.parse(createdAt));

    return textFile;
  }

  public static String buildHashKey(String fileId) {
    return FILE + SEPARATOR + fileId;
  }

  public static String getTaskIdsQueueName() {
    return TEXT_FILES + SEPARATOR + QUEUE_NAME;
  }

  public static String buildIpSetKey(String ip) {
    return IP + SEPARATOR + ip + SEPARATOR + LIST;
  }
}
