package clerk.util;

import static clerk.DataCollector.CollectionError;

import clerk.Clerk;
import clerk.DataCollector;
import clerk.DataProcessor;
import java.util.List;
import java.util.function.Supplier;

/**
 * A clerk that uses the same processor and collector for all sources. Type errors are checked at
 * runtime for out-of-the-box construction.
 */
public class SimpleClerk<O> implements Clerk<O> {
  private final Iterable<Supplier<?>> sources;
  private final DataProcessor<?, O> processor;
  private final DataCollector collector;

  private boolean isRunning;

  public SimpleClerk(Supplier<?> source, DataProcessor<?, O> processor, DataCollector collector) {
    this.sources = List.of(source);
    this.processor = processor;
    this.collector = collector;
  }

  public SimpleClerk(
      Iterable<Supplier<?>> sources, DataProcessor<?, O> processor, DataCollector collector) {
    this.sources = sources;
    this.processor = processor;
    this.collector = collector;
  }

  /** Starts collector from each source. */
  @Override
  public final void start() {
    if (!isRunning) {
      isRunning = true;
      for (Supplier<?> source : sources) {
        collectData(source, processor);
      }
    }
  }

  /** Stops collection. */
  @Override
  public final void stop() {
    if (isRunning) {
      collector.stop();
      isRunning = false;
    }
  }

  /** Return the processor's output. */
  @Override
  public final O read() {
    return processor.process();
  }

  private <I> void collectData(Supplier<?> source, DataProcessor<I, ?> processor) {
    try {
      collector.collect((Supplier<I>) source, processor);
    } catch (ClassCastException e) {
      throw new CollectionError(e);
    }
  }
}
