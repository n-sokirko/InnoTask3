package com.sokirko.port.state.impl;

import com.sokirko.port.entity.Berth;
import com.sokirko.port.entity.Port;
import com.sokirko.port.entity.Ship;
import com.sokirko.port.entity.Warehouse;
import com.sokirko.port.state.ShipState;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadingState implements ShipState {

  private static final Logger logger = LogManager.getLogger(LoadingState.class);
  private static final long MILLIS_PER_CONTAINER = 5;

  @Override
  public void handle(Ship ship) throws InterruptedException {
    int containersToLoad = ship.getContainersToLoad();
    int containersToUnload = ship.getContainersToUnload();
    if (containersToLoad > 0) {
      loadBatch(ship);
    } else if (containersToUnload > 0) {
      ship.setState(new UnloadingState());
    } else {
      ship.setState(new DepartingState());
    }
  }

  private void loadBatch(Ship ship) throws InterruptedException {
    Port port = Port.getInstance();
    Warehouse warehouse = port.getWarehouse();
    int containersToLoad = ship.getContainersToLoad();
    int freeSpace = ship.getFreeSpace();
    int requested = Math.min(containersToLoad, freeSpace);
    int loaded = warehouse.takeContainers(requested);
    if (loaded > 0) {
      TimeUnit.MILLISECONDS.sleep(loaded * MILLIS_PER_CONTAINER);
      ship.loadContainers(loaded);
      logger.info("{} loaded {} containers ({} left to load), {}",
          ship.getName(), loaded, ship.getContainersToLoad(), warehouse);
    } else {
      handleEmptyWarehouse(ship, port);
    }
  }

  private void handleEmptyWarehouse(Ship ship, Port port) {
    int containersToUnload = ship.getContainersToUnload();
    if (containersToUnload > 0) {
      logger.info("{} cannot load, warehouse is empty: unloads first to fill the warehouse", ship.getName());
      ship.setState(new UnloadingState());
    } else {
      Berth berth = ship.unmoor();
      port.releaseBerth(berth);
      logger.info("{} cannot load, warehouse is empty: frees {} and waits in line again",
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
    return "LOADING";
  }
}
