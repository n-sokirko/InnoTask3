package com.port.reader;

import com.port.exception.PortException;
import java.util.List;

public interface PortDataReader {

  List<String> readLines(String filePath) throws PortException;
}
