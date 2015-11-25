package bl;

import play.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerService {

    // TODO: set period to 60 seconds, after we've tested this thing.
    private static final long PERIOD = 5;

    private final ScheduledExecutorService exec =
            Executors.newSingleThreadScheduledExecutor();

    private final Solver solver = new GurobiSolver();

    // non-instantiable (other than here)
    private SchedulerService() { }


    /**
     * Starts the scheduler service.
     */
    public void start() {
        solver.initialize();
        exec.scheduleAtFixedRate(new SolveTask(solver), PERIOD, PERIOD, TimeUnit.SECONDS);
    }

    /**
     * Stops the scheduler service.
     */
    public void stop() {
        exec.shutdown();
    }


    /**
     * Singleton instance of the Scheduler Service.
     */
    public static final SchedulerService SINGLETON = new SchedulerService();


    /**
     * Inner class for the task that gets run periodically.
     * Basically, this makes sure the solver is ready, then pulls requests
     * off the queue, if any, and passes them to the solver to work with,
     * then kicks off the solver.
     */
    private static class SolveTask implements Runnable {

        private final Solver solver;

        private SolveTask(Solver solver) {
            this.solver = solver;
        }

        @Override
        public void run() {
            Logger.debug("scheduled task run.");
            if (solver.isReady()) {
                // TODO: implement...
            }
        }
    }
}