package clerk;

/** Interface that consumes and produces data. */
public interface Processor<I, O> {

  /** Adds data to the processor's storage. */
  default void add(I i) { }

  /** Returns the result of the processor on stored data. */
  O process();
}
