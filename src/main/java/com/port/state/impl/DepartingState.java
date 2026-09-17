package com.port.state.impl;

import com.port.entity.Berth;
import com.port.entity.Port;
import com.port.entity.Ship;
import com.port.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DepartingState implements ShipState {

  private static final Logger logger = LogManager.getLogger(DepartingState.class);

  @Override
  public void handle(Ship ship) {
    Port port = Port.getInstance();
    Berth berth = ship.unmoor();
    port.releaseBerth(berth);
    logger.info("{} departed from {} with {} containers on board",
        ship.getName(), berth, ship.getContainerCount());
    ship.setState(new ServedState());
  }

  @Override
  public boolean isFinal() {
    return false;
  }

  @Override
  public String toString() {
    return "DEPARTING";
  }
}
