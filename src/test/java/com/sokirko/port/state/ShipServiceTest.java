package com.sokirko.port.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sokirko.port.entity.Port;
import com.sokirko.port.entity.PortSettings;
import com.sokirko.port.entity.Ship;
import com.sokirko.port.entity.Warehouse;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ShipServiceTest {

  private static final PortSettings SETTINGS = new PortSettings(2, 50, 20);
  private static final int SHIP_CAPACITY = 40;
  private static final int ON_BOARD = 30;
  private static final int TO_UNLOAD = 30;
  private static final int TO_LOAD = 30;
  private static final int EXPECTED_ON_BOARD = ON_BOARD - TO_UNLOAD + TO_LOAD;

  @BeforeAll
  static void initializePort() {
    Port port = Port.getInstance();
    port.initialize(SETTINGS);
  }

  @Test
  void shouldLeaveShipWithLoadedContainersAfterService() throws InterruptedException {
    Ship ship = new Ship("Aurora", SHIP_CAPACITY, ON_BOARD, TO_UNLOAD, TO_LOAD);

    Ship servedShip = ship.call();

    assertEquals(EXPECTED_ON_BOARD, servedShip.getContainerCount());
  }

  @Test
  void shouldChangeWarehouseByUnloadedMinusLoaded() throws InterruptedException {
    Port port = Port.getInstance();
    Warehouse warehouse = port.getWarehouse();
    int containersBefore = warehouse.getContainerCount();
    Ship ship = new Ship("Neman", SHIP_CAPACITY, ON_BOARD, TO_UNLOAD, TO_LOAD);

    ship.call();

    assertEquals(containersBefore + TO_UNLOAD - TO_LOAD, warehouse.getContainerCount());
  }

  @Test
  void shouldServeEveryShipWhenBerthsAndWarehouseAreContended() throws Exception {
    List<Ship> ships = List.of(
        new Ship("Viking", 40, 40, 40, 10),
        new Ship("Sozh", 40, 35, 35, 0),
        new Ship("Baltica", 40, 0, 0, 30),
        new Ship("Polesie", 50, 10, 10, 40),
        new Ship("Berezina", 30, 25, 25, 20),
        new Ship("Svisloch", 45, 5, 5, 25));
    ExecutorService executor = Executors.newFixedThreadPool(ships.size());

    List<Future<Ship>> results = executor.invokeAll(ships);

    executor.shutdown();
    for (Future<Ship> result : results) {
      Ship ship = result.get();
      assertTrue(ship.isServed());
    }
  }
}
