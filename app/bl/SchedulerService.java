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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SchedulerService {

    // Tunable (compile time) period for solver to run, in seconds
    private static final long PERIOD = 30;

    public static ConcurrentLinkedQueue<StudentRequest> requestQueue = new ConcurrentLinkedQueue<>();

    private final ScheduledExecutorService exec =
            Executors.newSingleThreadScheduledExecutor();

    private final Solver solver = new GurobiSolver();

    public static final AtomicInteger batchNumber = new AtomicInteger(0);

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
        requestQueue.add(sr);
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
            int queuedRequestCount;

            Logger.debug("scheduled task run.");
            if (solver.isReady()) {
                // Need to associate requests with a solution...
                final int batch = SchedulerService.batchNumber.incrementAndGet();
                StudentRequest sr;
                List<StudentRequest> requests = new ArrayList<>(1);
                queuedRequestCount = requestQueue.size();

                for (int i = 0; i < queuedRequestCount; i++) {
                    // Patch the student record with the batch number and persist
                    sr = requestQueue.remove();
                    sr.batchNumber = batch;
                    Ebean.save(sr);
                    requests.add(sr);
                }

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
        }
    }
}