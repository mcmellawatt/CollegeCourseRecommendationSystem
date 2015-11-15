package bl;

import gurobi.*;
import models.Course;
import models.Student;

import java.util.List;

/**
 * Our ILP Solver implementation, using the Gurobi library.
 */
public class GurobiSolver implements Solver {

    // Currently '100' is just a place holder, the actual value for
    // maxClassSize will come from some business logic class. Ideally each
    // class would have a max class size, this can be addressed later.
    private static final int MAX_CLASS_SIZE = 100;

    private static final String FMT_SC = "si_%s_ci_%s";


    private GRBModel model;
    private List<Student> students;
    private List<Course> courses;


    /**
     * Constructs the Gurobi solver, initializing the model,
     * and retrieving the student and course lists.
     */
    public GurobiSolver() {

        // Initialize ILP model
        GRBEnv env;
        try {
            env = new GRBEnv();
            model = new GRBModel(env);
        } catch (GRBException e) {
            e.printStackTrace();
        }

        // Retrieve model data
        students = Student.findAll();
        courses = Course.findAll();
    }


    @Override
    public void optimize() {
        final int studentCount = students.size();
        final int courseCount = courses.size();

        try {
            // Gurobi matrix for students and courses for next semester.
            // This will be the solution.
            GRBVar[][] sc = new GRBVar[studentCount][courseCount];

            // Create GRBVar[][] sc variables;
            String s;
            for (int i = 0; i < studentCount; i++) {
                for (int j = 0; j < courseCount; j++) {
                    s = String.format(FMT_SC, students.get(i).id, courses.get(i).id);
                    sc[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, s);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
