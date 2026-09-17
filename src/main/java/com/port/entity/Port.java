package com.port.entity;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Port {

  private static final Logger logger = LogManager.getLogger(Port.class);
  private static final AtomicBoolean isInstanceCreated = new AtomicBoolean(false);
  private static final Lock instanceLock = new ReentrantLock();
  private static Port instance;

  private final AtomicBoolean isInitialized = new AtomicBoolean(false);
  private final Lock berthLock = new ReentrantLock();
  private final Deque<Berth> freeBerths = new ArrayDeque<>();
  private Semaphore berthPermits;
  private Warehouse warehouse;

  private Port() {
  }

  public static Port getInstance() {
    if (!isInstanceCreated.get()) {
      instanceLock.lock();
      try {
        if (instance == null) {
          instance = new Port();
          isInstanceCreated.set(true);
          logger.info("Port instance created");
        }
      } finally {
        instanceLock.unlock();
      }
    }
    return instance;
  }

  /**
   * Applies settings once. Repeated calls are ignored, so ships always see a stable port.
   *
   * @return true if these settings were applied, false if the port had already been initialized
   */
  public boolean initialize(PortSettings settings) {
    boolean isApplied = isInitialized.compareAndSet(false, true);
    if (isApplied) {
      int berthCount = settings.getBerthCount();
      berthLock.lock();
      try {
        for (int berthId = 1; berthId <= berthCount; berthId++) {
          freeBerths.addLast(new Berth(berthId));
        }
        berthPermits = new Semaphore(berthCount, true);
        warehouse = new Warehouse(settings.getWarehouseCapacity(), settings.getContainersInWarehouse());
      } finally {
        berthLock.unlock();
      }
      logger.info("Port initialized: {}", settings);
    } else {
      logger.warn("Port is already initialized, settings {} ignored", settings);
    }
    return isApplied;
  }

  public Berth acquireBerth() throws InterruptedException {
    berthPermits.acquire();
    berthLock.lock();
    try {
      return freeBerths.pollFirst();
    } finally {
      berthLock.unlock();
    }
  }

  public void releaseBerth(Berth berth) {
    berthLock.lock();
    try {
      freeBerths.addLast(berth);
    } finally {
      berthLock.unlock();
    }
    berthPermits.release();
  }

  public Warehouse getWarehouse() {
    return warehouse;
  }

  public boolean isInitialized() {
    return isInitialized.get();
  }
}
