package com.port.reader.impl;

import com.port.exception.PortException;
import com.port.reader.PortDataReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextPortDataReader implements PortDataReader {

  private static final Logger logger = LogManager.getLogger(TextPortDataReader.class);
  private static final String COMMENT_PREFIX = "#";

  @Override
  public List<String> readLines(String filePath) throws PortException {
    Path path = Path.of(filePath);
    try {
      List<String> allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
      List<String> dataLines = new ArrayList<>();
      for (String rawLine : allLines) {
        String line = rawLine.strip();
        if (!line.isEmpty() && !line.startsWith(COMMENT_PREFIX)) {
          dataLines.add(line);
        }
      }
      logger.info("Read {} data lines from {}", dataLines.size(), filePath);
      return dataLines;
    } catch (IOException e) {
      throw new PortException("Cannot read port data file " + filePath, e);
    }
  }
}
