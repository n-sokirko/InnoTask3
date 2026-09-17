# Task 3. Multithreading — Port

Ships (`Callable`) arrive at the port, moor to a free berth (one ship per berth),
unload containers into the port warehouse and load containers from it.
The warehouse and ships never exceed their capacity; every ship is served.

## Run

```
mvn test
mvn compile exec:java
```

Input data: `data/port.txt`. Logs: console + `logs/app.log`.

## Concurrency

| Resource | Tool |
|---|---|
| `Port` singleton | `ReentrantLock` + `AtomicBoolean` (lazy, thread-safe, no enum / holder class) |
| Berths | fair `Semaphore` + `ReentrantLock` over the free-berth deque |
| Warehouse | `ReentrantLock` + `Condition` (`spaceAvailable`, `containersAvailable`) with timed waits |
| Delays | `TimeUnit.MILLISECONDS.sleep` |

## Ship states (State pattern)

`WAITING_FOR_BERTH -> UNLOADING <-> LOADING -> DEPARTING -> SERVED`

- Warehouse full while unloading: the ship loads first (if it can), otherwise frees the berth and queues again.
- Warehouse empty while loading: the ship unloads first (if it can), otherwise frees the berth and queues again.

So a ship never blocks a berth forever, and the others can make progress.
