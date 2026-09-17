package com.sokirko.port.entity;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

class PortTest {

  private static final int THREAD_COUNT = 50;

  @Test
  void shouldReturnSameInstanceFromAllThreads() throws Exception {
    Port expected = Port.getInstance();
    List<Callable<Port>> tasks = new ArrayList<>();
    for (int i = 0; i < THREAD_COUNT; i++) {
      tasks.add(Port::getInstance);
    }
    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

    List<Future<Port>> results = executor.invokeAll(tasks);

    executor.shutdown();
    for (Future<Port> result : results) {
      assertSame(expected, result.get());
    }
  }
}
