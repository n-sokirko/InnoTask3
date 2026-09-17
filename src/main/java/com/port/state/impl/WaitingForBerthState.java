package com.port.state.impl;

import com.port.entity.Berth;
import com.port.entity.Port;
import com.port.entity.Ship;
import com.port.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WaitingForBerthState implements ShipState {

  private static final Logger logger = LogManager.getLogger(WaitingForBerthState.class);

  @Override
  public void handle(Ship ship) throws InterruptedException {
    Port port = Port.getInstance();
    Berth berth = port.acquireBerth();
    ship.moor(berth);
    logger.info("{} moored at {}", ship.getName(), berth);
    ship.setState(new UnloadingState());
  }

  @Override
  public boolean isFinal() {
    return false;
  }

  @Override
  public String toString() {
    return "WAITING_FOR_BERTH";
  }
}
