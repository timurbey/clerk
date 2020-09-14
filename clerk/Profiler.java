package clerk;

import clerk.concurrent.TaskRunner;
import java.util.function.Supplier;
import java.util.logging.Logger;
import javax.inject.Inject;

/** Manages a system that collects and processes data through a user API. */
public final class Profiler<I, O> {
  private final Iterable<Supplier<I>> sources;
  private final Processor<I, O> processor;
  private final TaskRunner executor;

  private boolean isRunning = false;

  @Inject
  Profiler(
    @DataSource Iterable<Supplier<I>> sources,
    Processor<I, O> processor,
    TaskRunner executor) {
      this.sources = sources;
      this.processor = processor;
      this.executor = executor;
  }

  /**
   * Starts running the profiler, which feeds the output of the data sources
   * into the processor. All sources are sampled based on the sampler.
   *
   * NOTE: the profiler will ignore this call if it is already running.
   */
  public void start() {
    if (!isRunning) {
      for (Supplier<I> source: sources) {
        executor.start(() -> processor.accept(source.get()));
      }
      isRunning = true;
    }
  }

  /**
   * Stops running the profiler by canceling all scheduled tasks and dumping
   * the stored data.
   *
   * NOTE: the profiler will ignore this call if it is not running.
   */
  public O stop() {
    if (isRunning) {
      executor.stop();
      return processor.get();
    } else {
      return null;
    }
  }
}