package com.example.fileprocessor.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.fileprocessor.api.FilesApi;
import com.example.fileprocessor.model.NewFile;
import com.example.fileprocessor.model.TextFile;
import com.example.fileprocessor.service.TextFileService;
import com.example.fileprocessor.util.TextFileGenerator;

/**
 * @author Adobe Systems Incorporated.
 */
@RestController
public class TextFileProcessorController implements FilesApi {

  private final TextFileService textFileService;
  private final TextFileGenerator taskFileGenerator;

  @Autowired
  public TextFileProcessorController(
    TextFileService textFileService,
    TextFileGenerator taskFileGenerator) {
    this.textFileService = textFileService;
    this.taskFileGenerator = taskFileGenerator;
  }

  @Override
  public ResponseEntity<String> addTextFile(NewFile newFile) {
    final TextFile textFile = taskFileGenerator.apply(newFile);

    final Long saveFileResponseCode = textFileService.addFileToQueue(textFile);

    if (saveFileResponseCode.equals(-1L)) {
      return new ResponseEntity<>("Unable to save the file id: " + newFile.getFileId(), HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      return new ResponseEntity<>("File was added to the processing queue. TaskId: " + textFile.getTaskId(), HttpStatus.OK);
    }
  }

  @Override
  public ResponseEntity<TextFile> getFileById(String taskId) {
    final Optional<TextFile> file = textFileService.getFile(taskId);

    return file.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

}
