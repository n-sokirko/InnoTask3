package com.sokirko.port.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

class WarehouseTest {

  private static final int CAPACITY = 100;
  private static final int INITIAL_CONTAINERS = 40;
  private static final long WAIT_MILLIS = 50;
  private static final int SMALL_BATCH = 25;
  private static final int HUGE_BATCH = 500;
  private static final int WORKER_COUNT = 20;
  private static final int WORKER_BATCH = 10;

  @Test
  void shouldStoreRequestedContainersWhenSpaceIsEnough() throws InterruptedException {
    Warehouse warehouse = new Warehouse(CAPACITY, INITIAL_CONTAINERS, WAIT_MILLIS);

    int stored = warehouse.storeContainers(SMALL_BATCH);

    assertEquals(SMALL_BATCH, stored);
  }

  @Test
  void shouldStoreOnlyFreeSpaceWhenBatchExceedsCapacity() throws InterruptedException {
    Warehouse warehouse = new Warehouse(CAPACITY, INITIAL_CONTAINERS, WAIT_MILLIS);

    int stored = warehouse.storeContainers(HUGE_BATCH);

    assertEquals(CAPACITY - INITIAL_CONTAINERS, stored);
  }

  @Test
  void shouldStoreNothingWhenWarehouseStaysFull() throws InterruptedException {
    Warehouse warehouse = new Warehouse(CAPACITY, CAPACITY, WAIT_MILLIS);

    int stored = warehouse.storeContainers(SMALL_BATCH);

    assertEquals(0, stored);
  }

  @Test
  void shouldTakeOnlyAvailableContainers() throws InterruptedException {
    Warehouse warehouse = new Warehouse(CAPACITY, INITIAL_CONTAINERS, WAIT_MILLIS);

    int taken = warehouse.takeContainers(HUGE_BATCH);

    assertEquals(INITIAL_CONTAINERS, taken);
  }

  @Test
  void shouldTakeNothingWhenWarehouseStaysEmpty() throws InterruptedException {
    Warehouse warehouse = new Warehouse(CAPACITY, 0, WAIT_MILLIS);

    int taken = warehouse.takeContainers(SMALL_BATCH);

    assertEquals(0, taken);
  }

  @Test
  void shouldNeverExceedCapacityUnderConcurrentStoring() throws Exception {
    Warehouse warehouse = new Warehouse(CAPACITY, INITIAL_CONTAINERS, WAIT_MILLIS);
    List<Callable<Integer>> workers = new ArrayList<>();
    for (int i = 0; i < WORKER_COUNT; i++) {
      workers.add(() -> warehouse.storeContainers(WORKER_BATCH));
    }
    ExecutorService executor = Executors.newFixedThreadPool(WORKER_COUNT);

    List<Future<Integer>> results = executor.invokeAll(workers);

    executor.shutdown();
    int storedTotal = 0;
    for (Future<Integer> result : results) {
      storedTotal += result.get();
    }
    assertEquals(CAPACITY - INITIAL_CONTAINERS, storedTotal);
    assertTrue(warehouse.getContainerCount() <= CAPACITY);
  }
}
