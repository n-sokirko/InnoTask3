package com.sokirko.port.state.impl;

import com.sokirko.port.entity.Berth;
import com.sokirko.port.entity.Port;
import com.sokirko.port.entity.Ship;
import com.sokirko.port.entity.Warehouse;
import com.sokirko.port.state.ShipState;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UnloadingState implements ShipState {

  private static final Logger logger = LogManager.getLogger(UnloadingState.class);
  private static final long MILLIS_PER_CONTAINER = 5;

  @Override
  public void handle(Ship ship) throws InterruptedException {
    if (ship.getContainersToUnload() > 0) {
      unloadBatch(ship);
    } else {
      ship.setState(new LoadingState());
    }
  }

  private void unloadBatch(Ship ship) throws InterruptedException {
    Port port = Port.getInstance();
    Warehouse warehouse = port.getWarehouse();
    int containersToUnload = ship.getContainersToUnload();
    int unloaded = warehouse.storeContainers(containersToUnload);
    if (unloaded > 0) {
      TimeUnit.MILLISECONDS.sleep(unloaded * MILLIS_PER_CONTAINER);
      ship.unloadContainers(unloaded);
      logger.info("{} unloaded {} containers ({} left to unload), {}",
          ship.getName(), unloaded, ship.getContainersToUnload(), warehouse);
    } else {
      handleFullWarehouse(ship, port);
    }
  }

  private void handleFullWarehouse(Ship ship, Port port) {
    int containersToLoad = ship.getContainersToLoad();
    int freeSpace = ship.getFreeSpace();
    if (containersToLoad > 0 && freeSpace > 0) {
      logger.info("{} cannot unload, warehouse is full: loads first to free the warehouse", ship.getName());
      ship.setState(new LoadingState());
    } else {
      Berth berth = ship.unmoor();
      port.releaseBerth(berth);
      logger.info("{} cannot unload, warehouse is full: frees {} and waits in line again",
          ship.getName(), berth);
      ship.setState(new WaitingForBerthState());
    }
  }

  @Override
  public boolean isFinal() {
    return false;
  }

  @Override
  public String toString() {
    return "UNLOADING";
  }
}
