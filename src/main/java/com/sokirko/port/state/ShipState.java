package com.sokirko.port.state;

import com.sokirko.port.entity.Ship;

public interface ShipState {

  /**
   * Performs the work of the current stage and moves the ship to the next state.
   */
  void handle(Ship ship) throws InterruptedException;

  boolean isFinal();
}
