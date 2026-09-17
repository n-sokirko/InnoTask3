package com.sokirko.port.entity;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Warehouse {

  private static final Logger logger = LogManager.getLogger(Warehouse.class);
  private static final long DEFAULT_WAIT_MILLIS = 300;

  private final int capacity;
  private final long waitMillis;
  private final Lock lock = new ReentrantLock(true);
  private final Condition spaceAvailable = lock.newCondition();
  private final Condition containersAvailable = lock.newCondition();
  private int containerCount;

  public Warehouse(int capacity, int containerCount) {
    this(capacity, containerCount, DEFAULT_WAIT_MILLIS);
  }

  public Warehouse(int capacity, int containerCount, long waitMillis) {
    this.capacity = capacity;
    this.containerCount = containerCount;
    this.waitMillis = waitMillis;
  }

  /**
   * Stores up to {@code requested} containers. Waits a limited time while the warehouse is full.
   *
   * @return how many containers were actually stored, 0 if no space appeared in time
   */
  public int storeContainers(int requested) throws InterruptedException {
    lock.lock();
    try {
      long remainingNanos = TimeUnit.MILLISECONDS.toNanos(waitMillis);
      while (containerCount == capacity && remainingNanos > 0) {
        remainingNanos = spaceAvailable.awaitNanos(remainingNanos);
      }
      int freeSpace = capacity - containerCount;
      int stored = Math.min(requested, freeSpace);
      containerCount += stored;
      if (stored > 0) {
        containersAvailable.signalAll();
        logger.debug("Stored {} containers, warehouse now {}/{}", stored, containerCount, capacity);
      }
      return stored;
    } finally {
      lock.unlock();
    }
  }

  /**
   * Takes up to {@code requested} containers. Waits a limited time while the warehouse is empty.
   *
   * @return how many containers were actually taken, 0 if none appeared in time
   */
  public int takeContainers(int requested) throws InterruptedException {
    lock.lock();
    try {
      long remainingNanos = TimeUnit.MILLISECONDS.toNanos(waitMillis);
      while (containerCount == 0 && remainingNanos > 0) {
        remainingNanos = containersAvailable.awaitNanos(remainingNanos);
      }
      int taken = Math.min(requested, containerCount);
      containerCount -= taken;
      if (taken > 0) {
        spaceAvailable.signalAll();
        logger.debug("Took {} containers, warehouse now {}/{}", taken, containerCount, capacity);
      }
      return taken;
    } finally {
      lock.unlock();
    }
  }

  public int getContainerCount() {
    lock.lock();
    try {
      return containerCount;
    } finally {
      lock.unlock();
    }
  }

  public int getCapacity() {
    return capacity;
  }

  @Override
  public String toString() {
    return "Warehouse{" + getContainerCount() + "/" + capacity + '}';
  }
}
