package com.sokirko.port.parser.impl;

import com.sokirko.port.entity.PortSettings;
import com.sokirko.port.entity.Ship;
import com.sokirko.port.exception.PortException;
import com.sokirko.port.parser.PortDataParser;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextPortDataParser implements PortDataParser {

  private static final Logger logger = LogManager.getLogger(TextPortDataParser.class);
  private static final String DELIMITER_REGEX = "\\s+";
  private static final String PORT_LINE_REGEX = "^port(\\s+\\d+){3}$";
  private static final String SHIP_LINE_REGEX = "^ship\\s+\\S+(\\s+\\d+){4}$";

  @Override
  public PortSettings parsePortSettings(List<String> lines) throws PortException {
    String portLine = findPortLine(lines);
    String[] tokens = portLine.split(DELIMITER_REGEX);
    int berthCount = Integer.parseInt(tokens[1]);
    int warehouseCapacity = Integer.parseInt(tokens[2]);
    int containersInWarehouse = Integer.parseInt(tokens[3]);
    PortSettings settings = new PortSettings(berthCount, warehouseCapacity, containersInWarehouse);
    logger.debug("Parsed {}", settings);
    return settings;
  }

  @Override
  public List<Ship> parseShips(List<String> lines) {
    List<Ship> ships = new ArrayList<>();
    for (String line : lines) {
      if (line.matches(SHIP_LINE_REGEX)) {
        Ship ship = parseShip(line);
        ships.add(ship);
      }
    }
    logger.debug("Parsed {} ships", ships.size());
    return ships;
  }

  private String findPortLine(List<String> lines) throws PortException {
    for (String line : lines) {
      if (line.matches(PORT_LINE_REGEX)) {
        return line;
      }
    }
    throw new PortException("Port settings line is missing");
  }

  private Ship parseShip(String line) {
    String[] tokens = line.split(DELIMITER_REGEX);
    String name = tokens[1];
    int capacity = Integer.parseInt(tokens[2]);
    int containerCount = Integer.parseInt(tokens[3]);
    int containersToUnload = Integer.parseInt(tokens[4]);
    int containersToLoad = Integer.parseInt(tokens[5]);
    return new Ship(name, capacity, containerCount, containersToUnload, containersToLoad);
  }
}
