package clerk;

import java.util.function.Consumer;
import java.util.function.Supplier;

/** Interface that consumes and produces data. */
public interface Processor<I, O> extends Consumer<I>, Supplier<O> { }