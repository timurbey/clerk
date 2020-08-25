package clerk.concurrent;

import static java.lang.Math.min;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import clerk.core.Scheduler;
import java.util.concurrent.ScheduledExecutorService;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;

/** Scheduler that periodically runs tasks on an scheduled executor service. */
public final class PeriodicExecutionScheduler implements Scheduler {
  private final ScheduledExecutorService executor;
  private final Duration period;

  @Inject
  PeriodicExecutionScheduler(@SchedulingRate Duration period, ScheduledExecutorService executor) {
    this.period = period;
    this.executor = executor;
  }

  /** Starts a task that will be rescheduled. */
  @Override
  public void schedule(Runnable r) {
    executor.execute(() -> runAndReschedule(r));
  }

  /** Terminates all running threads. */
  @Override
  public void cancel() {
    executor.shutdown();
  }

  /** Runs the workload and then schedules it to run at the next period start. */
  private void runAndReschedule(Runnable r) {
    Instant start = Instant.now();
    r.run();
    Duration sleepTime = period.minus(Duration.between(start, Instant.now()));

    if (sleepTime.toMillis() > 0) {
      executor.schedule(() -> runAndReschedule(r), sleepTime.toMillis(), MILLISECONDS);
    } else if (sleepTime.toNanos() > 0) {
      executor.schedule(() -> runAndReschedule(r), sleepTime.toNanos(), NANOSECONDS);
    } else {
      executor.execute(() -> runAndReschedule(r));
    }
  }
}