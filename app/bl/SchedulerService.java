package bl;

import com.avaje.ebean.Ebean;
import models.Course;
import models.StudentSolution;
import models.Student;
import models.StudentRequest;
import play.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class SchedulerService {

    // Tunable (compile time) period for solver to run, in seconds
    private static final long PERIOD = 30;

    private final ScheduledExecutorService exec =
            Executors.newSingleThreadScheduledExecutor();

    private final Solver solver = new GurobiSolver();

    private final AtomicInteger batchNumber = new AtomicInteger(0);

    // non-instantiable (other than here)
    private SchedulerService() { }


    /**
     * Starts the scheduler service.
     */
    public void start() {
        solver.initialize();
        // TODO: Reinstate the following, once the task does something useful
//        exec.scheduleAtFixedRate(new SolveTask(solver), PERIOD, PERIOD, SECONDS);
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
        // This method should simply put the request on the queue
        // But until we HAVE a queue, we'll fake it

        // TODO: move all of the following code into the periodic task

        //  this will be replaced by pulling requests off queue, eventually
        List<StudentRequest> requests = new ArrayList<>(1);
        requests.add(sr);

        // Need to associate requests with a solution...
        final int batch = batchNumber.incrementAndGet();

        // Patch the student record with the batch number and persist
        sr.batchNumber = batch;
        Ebean.save(sr);

        // Get the solver to re-adjust its view of the world
        solver.adjustConstraints(requests);

        // Get a solution
        Map<Student, List<Course>> result = solver.solve();

        // Persist the solution
        List<StudentSolution> solutionRecords = new ArrayList<>(result.size());
        for (Map.Entry<Student, List<Course>> entry: result.entrySet()) {
            StudentSolution solution = new StudentSolution();
            solution.batchNumber = batch;
            solution.student = entry.getKey();
            solution.recommendedCourses.addAll(entry.getValue());
            solutionRecords.add(solution);
        }
        Ebean.save(solutionRecords);
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