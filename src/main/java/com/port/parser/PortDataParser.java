package com.port.parser;

import com.port.entity.PortSettings;
import com.port.entity.Ship;
import com.port.exception.PortException;
import java.util.List;

public interface PortDataParser {

  PortSettings parsePortSettings(List<String> lines) throws PortException;

  List<Ship> parseShips(List<String> lines);
}
