package clerk;

import clerk.concurrent.ParallelExecutorModule;
import chappie.core.Profiler;
import clerk.sampling.SamplingRateModule;
import clerk.sampling.RuntimeSamplingModule;
import dagger.Component;
import java.util.ArrayList;

// temporary driver; should be able to get rid of this in some way or another
// some reading indicates that we would use a template to define these modules
// could look into an annotation for a profiler.
public class Clerk {
  @Component(modules = {
    ParallelExecutorModule.class,
    SamplingRateModule.class,
    RuntimeSamplingModule.class
  })
  interface ProfilerFactory {
    Profiler newProfiler();
  }

  private static final ProfilerFactory profilerFactory = DaggerChappie_ProfilerFactory.builder().build();

  private static Profiler profiler;
  private static Iterable<Profile> profiles = new ArrayList<Profile>();

  // starts a profiler if there is not one already
  public static void start() {
    if (profiler == null) {
      profiles = new ArrayList<Profile>();
      profiler = profilerFactory.newProfiler();
      profiler.start();
    }
  }

  // stops the profiler if there is one
  public static void stop() {
    if (profiler != null) {
      profiler.stop();
      profiles = profiler.getProfiles();
      profiler = null;
    }
  }

  // get all profiles stored
  public static Iterable<Profile> getProfiles() {
    if (profiler != null) {
      profiles = profiler.getProfiles();
    }
    return profiles;
  }
}
