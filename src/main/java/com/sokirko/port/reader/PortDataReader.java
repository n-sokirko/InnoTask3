package com.sokirko.port.reader;

import com.sokirko.port.exception.PortException;
import java.util.List;

public interface PortDataReader {

  List<String> readLines(String filePath) throws PortException;
}
