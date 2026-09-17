package com.port.state;

import com.port.entity.Ship;

public interface ShipState {

  /**
   * Performs the work of the current stage and moves the ship to the next state.
   */
  void handle(Ship ship) throws InterruptedException;

  boolean isFinal();
}
