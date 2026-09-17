package com.sokirko.port.entity;

import com.sokirko.port.state.ShipState;
import com.sokirko.port.state.impl.WaitingForBerthState;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Ship implements Callable<Ship> {

  private static final Logger logger = LogManager.getLogger(Ship.class);

  private final String name;
  private final int capacity;
  private int containerCount;
  private int containersToUnload;
  private int containersToLoad;
  private ShipState state;
  private Berth berth;

  public Ship(String name, int capacity, int containerCount, int containersToUnload, int containersToLoad) {
    this.name = name;
    this.capacity = capacity;
    this.containerCount = containerCount;
    this.containersToUnload = containersToUnload;
    this.containersToLoad = containersToLoad;
    this.state = new WaitingForBerthState();
  }

  @Override
  public Ship call() throws InterruptedException {
    logger.info("{} arrived at the roadstead: {}", name, this);
    try {
      while (!state.isFinal()) {
        state.handle(this);
      }
    } finally {
      if (berth != null) {
        Port port = Port.getInstance();
        port.releaseBerth(berth);
        logger.warn("{} left {} before being served", name, berth);
        berth = null;
      }
    }
    return this;
  }

  public void moor(Berth berth) {
    this.berth = berth;
  }

  public Berth unmoor() {
    Berth releasedBerth = berth;
    berth = null;
    return releasedBerth;
  }

  public void unloadContainers(int count) {
    containerCount -= count;
    containersToUnload -= count;
  }

  public void loadContainers(int count) {
    containerCount += count;
    containersToLoad -= count;
  }

  public int getFreeSpace() {
    return capacity - containerCount;
  }

  public boolean isServed() {
    return state.isFinal();
  }

  public String getName() {
    return name;
  }

  public int getCapacity() {
    return capacity;
  }

  public int getContainerCount() {
    return containerCount;
  }

  public int getContainersToUnload() {
    return containersToUnload;
  }

  public int getContainersToLoad() {
    return containersToLoad;
  }

  public ShipState getState() {
    return state;
  }

  public void setState(ShipState state) {
    logger.debug("{}: {} -> {}", name, this.state, state);
    this.state = state;
  }

  public Berth getBerth() {
    return berth;
  }

  @Override
  public String toString() {
    return "Ship{name='" + name + '\''
        + ", capacity=" + capacity
        + ", containerCount=" + containerCount
        + ", containersToUnload=" + containersToUnload
        + ", containersToLoad=" + containersToLoad
        + ", state=" + state + '}';
  }
}
