package bl;

import models.StudentRequest;
import play.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerService {

    // Tunable (compile time) period for solver to run, in seconds
    private static final long PERIOD = 30;

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
     * Submits a student request to the solver
     *
     * @param sr student request
     */
    public void submitRequest(StudentRequest sr) {
        // TODO: temp code to wrap request in list.
        //  this will be replaced by pulling requests off queue, eventually
        List<StudentRequest> requests = new ArrayList<>(1);
        requests.add(sr);

        // TODO: temp code to invoke the solver -- should be done from periodic task
        solver.adjustConstraints(requests);
        solver.solve();
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