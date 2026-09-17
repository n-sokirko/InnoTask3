package com.sokirko.port.app;

import com.sokirko.port.entity.Port;
import com.sokirko.port.entity.PortSettings;
import com.sokirko.port.entity.Ship;
import com.sokirko.port.entity.Warehouse;
import com.sokirko.port.exception.PortException;
import com.sokirko.port.parser.PortDataParser;
import com.sokirko.port.parser.impl.TextPortDataParser;
import com.sokirko.port.reader.PortDataReader;
import com.sokirko.port.reader.impl.TextPortDataReader;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PortApplication {

  private static final Logger logger = LogManager.getLogger(PortApplication.class);
  private static final String DATA_FILE_PATH = "data/port.txt";

  public static void main(String[] args) {
    PortDataReader reader = new TextPortDataReader();
    PortDataParser parser = new TextPortDataParser();
    try {
      List<String> lines = reader.readLines(DATA_FILE_PATH);
      PortSettings settings = parser.parsePortSettings(lines);
      Port port = Port.getInstance();
      port.initialize(settings);
      List<Ship> ships = parser.parseShips(lines);
      runShips(ships);
      Warehouse warehouse = port.getWarehouse();
      logger.info("All ships processed, final state: {}", warehouse);
    } catch (PortException e) {
      logger.error("Port simulation failed", e);
    } catch (ExecutionException e) {
      logger.error("A ship failed during service", e);
    } catch (InterruptedException e) {
      logger.error("Port simulation interrupted", e);
      Thread mainThread = Thread.currentThread();
      mainThread.interrupt();
    }
  }

  private static void runShips(List<Ship> ships) throws InterruptedException, ExecutionException {
    ExecutorService executor = Executors.newFixedThreadPool(ships.size());
    try {
      List<Future<Ship>> futures = executor.invokeAll(ships);
      for (Future<Ship> future : futures) {
        Ship ship = future.get();
        logger.info("Served: {}", ship);
      }
    } finally {
      executor.shutdown();
    }
  }
}
