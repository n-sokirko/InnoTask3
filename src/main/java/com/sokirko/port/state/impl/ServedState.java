package com.sokirko.port.state.impl;

import com.sokirko.port.entity.Ship;
import com.sokirko.port.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServedState implements ShipState {

  private static final Logger logger = LogManager.getLogger(ServedState.class);

  @Override
  public void handle(Ship ship) {
    logger.warn("{} is already served, nothing to do", ship.getName());
  }

  @Override
  public boolean isFinal() {
    return true;
  }

  @Override
  public String toString() {
    return "SERVED";
  }
}
