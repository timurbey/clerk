package clerk.profilers.timer;

import clerk.core.DataProcessor;
import clerk.core.Scheduler;
import dagger.Module;
import dagger.Provides;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.function.Supplier;

/** Module to time how long the profiler has run. */
@Module
public interface TimerModule {
  @Provides
  static Iterable<Supplier<Void>> provideSources() {
    return Collections.emptyList();
  }

  @Provides
  static DataProcessor<Void, Duration> provideProcessor() {
    return new DataProcessor<Void, Duration>() {
      private Instant start = Instant.now();

      @Override
      public Duration process() {
        return Duration.between(start, Instant.now());
      }
    };
  }

  @Provides
  static Scheduler provideScheduler() {
    return new Scheduler() {
      @Override
      public void schedule(Runnable r) { }

      @Override
      public void cancel() { }
    };
  }
}
