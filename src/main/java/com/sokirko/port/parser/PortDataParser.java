package com.sokirko.port.parser;

import com.sokirko.port.entity.PortSettings;
import com.sokirko.port.entity.Ship;
import com.sokirko.port.exception.PortException;
import java.util.List;

public interface PortDataParser {

  PortSettings parsePortSettings(List<String> lines) throws PortException;

  List<Ship> parseShips(List<String> lines);
}
