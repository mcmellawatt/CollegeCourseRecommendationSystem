package bl;

import models.Course;
import models.Student;
import models.StudentRequest;

import java.util.List;
import java.util.Map;

/**
 * Solver interface.
 */
public interface Solver {

    /**
     * Returns true if the solver is ready and able to process requests.
     *
     * @return true if ready
     */
    boolean isReady();

    /**
     * Initializes the solver's internal data structures.
     */
    void initialize();

    /**
     * Instructs the solver to adjust its constraints, based on the new
     * batch of student requests.
     *
     * @param requests batch of student requests
     */
    void adjustConstraints(List<StudentRequest> requests);

    /**
     * Produces a solution for the current set of requests and constraints.
     * Returns a map of student to list of courses that the student is
     * recommended to take.
     *
     * @return student to course recommendations map
     */
    Map<Student, List<Course>> solve();
}