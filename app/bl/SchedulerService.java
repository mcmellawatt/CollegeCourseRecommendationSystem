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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SchedulerService {

    // Tunable (compile time) period for solver to run, in seconds
    private static final long PERIOD = 30;

    private final Queue<StudentRequest> requestQueue =
            new ConcurrentLinkedQueue<>();

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
        Logger.debug("Starting up periodic task ({}s period)", PERIOD);
        exec.scheduleAtFixedRate(new SolveTask(solver, requestQueue, batchNumber),
                PERIOD, PERIOD, TimeUnit.SECONDS);
    }

    /**
     * Stops the scheduler service.
     */
    public void stop() {
        Logger.debug("Shutting down task executor");
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
        private final AtomicInteger batchNumber;
        private final Queue<StudentRequest> requestQueue;

        private SolveTask(Solver solver,
                          Queue<StudentRequest> requestQueue,
                          AtomicInteger batchNumber) {
            this.solver = solver;
            this.requestQueue = requestQueue;
            this.batchNumber = batchNumber;
        }

        @Override
        public void run() {
            Logger.debug("Scheduled Task run - checking for work...");

            if (!solver.isReady()) {
                Logger.debug(" -- Solver not ready");
                return;
            }

            final int nRequests = requestQueue.size();
            if (nRequests == 0) {
                Logger.debug(" -- No requests queued");
                return;
            }

            Logger.debug(" -- Solver ready and {} request(s) queued", nRequests);

            // Need to associate requests with a solution...
            final int batch = batchNumber.incrementAndGet();

            // Pull requests off the queue
            List<StudentRequest> requests = dequeue(nRequests, batch);

            // Get the solver to re-adjust its view of the world
            solver.adjustConstraints(requests);

            // Get a solution and persist it
            persistSolution(solver.solve(), batch);
        }


        // remove requests from queue, and patch each with batch number
        private List<StudentRequest> dequeue(int nRequests, int batch) {
            List<StudentRequest> requests = new ArrayList<>(nRequests);
            for (int i = 0; i < nRequests; i++) {
                StudentRequest sr = requestQueue.remove();
                sr.batchNumber = batch;
                sr.save();
                requests.add(sr);
            }
            Logger.debug("Dequeued {} request(s)", nRequests);
            return requests;
        }

        // create and persist solution records
        private void persistSolution(Map<Student, List<Course>> results,
                                     int batch) {
            if (results.isEmpty()) {
                Logger.debug("No solution results for batch {}", batch);
                return;
            }

            // create solution records
            final int nSolns = results.size();
            List<StudentSolution> solutionRecords = new ArrayList<>(nSolns);
            for (Map.Entry<Student, List<Course>> entry: results.entrySet()) {
                StudentSolution solution = new StudentSolution();
                solution.batchNumber = batch;
                solution.student = entry.getKey();
                solution.recommendedCourses.addAll(entry.getValue());
                solutionRecords.add(solution);
            }

            // persist them
            Logger.debug("Persisting {} solution record(s) (batch {})",
                    nSolns, batch);
            Ebean.save(solutionRecords);
        }
    }
}