package com.example.fileprocessor.util;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.example.fileprocessor.model.NewFile;
import com.example.fileprocessor.model.TextFile;

import static com.example.fileprocessor.model.TextFile.TaskStatusEnum.CREATED;

/**
 * @author Adobe Systems Incorporated.
 */

@Component
public class TextFileGenerator implements Function<NewFile, TextFile> {
  private final List<String> arrays = Arrays.asList("127.0.0.1", "192.168.1.1");

  @Override
  public TextFile apply(NewFile newFile) {

    final TextFile textFile = new TextFile();
    textFile.setFileId(newFile.getFileId());
    textFile.setIps(arrays);
    textFile.setTaskStatus(CREATED);
    textFile.setTaskId(UUID.randomUUID().toString());
    textFile.setTaskCreationDate(LocalDate.now());

    return textFile;
  }
}
