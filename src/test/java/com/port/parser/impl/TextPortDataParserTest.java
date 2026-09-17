package com.port.parser.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.port.entity.PortSettings;
import com.port.entity.Ship;
import com.port.exception.PortException;
import java.util.List;
import org.junit.jupiter.api.Test;

class TextPortDataParserTest {

  private static final List<String> LINES = List.of(
      "port 3 100 40",
      "ship Aurora 60 50 50 20",
      "ship Baltica 40 0 0 35");
  private static final PortSettings EXPECTED_SETTINGS = new PortSettings(3, 100, 40);
  private static final int EXPECTED_SHIP_COUNT = 2;
  private static final String EXPECTED_FIRST_SHIP_NAME = "Aurora";

  private final TextPortDataParser parser = new TextPortDataParser();

  @Test
  void shouldParsePortSettings() throws PortException {
    List<String> lines = LINES;

    PortSettings settings = parser.parsePortSettings(lines);

    assertEquals(EXPECTED_SETTINGS, settings);
  }

  @Test
  void shouldParseEveryShipLine() {
    List<String> lines = LINES;

    List<Ship> ships = parser.parseShips(lines);

    assertEquals(EXPECTED_SHIP_COUNT, ships.size());
  }

  @Test
  void shouldParseShipName() {
    List<String> lines = LINES;

    List<Ship> ships = parser.parseShips(lines);

    Ship firstShip = ships.get(0);
    assertEquals(EXPECTED_FIRST_SHIP_NAME, firstShip.getName());
  }
}
