package bl;

import gurobi.*;
import models.Course;
import models.Student;

import java.util.List;

/**
 * Our ILP Solver implementation, using the Gurobi library.
 */
public class GurobiSolver implements Solver {

    // Currently '30' is just a place holder, the actual value for
    // maxClassSize will come from some business logic class. Ideally each
    // class would have a max class size, this can be addressed later.
    private static final int MAX_CLASS_SIZE = 30;

    private static final String FMT_SC = "si_%s_ci_%s";


    private GRBModel model;
    private List<Student> students;
    private List<Course> courses;

    final int studentCount = students.size();
    final int courseCount = courses.size();

    // Gurobi matrix for students and courses for next semester.
    // This will be the solution.
    private SCVars scVars = new SCVars(studentCount, courseCount);

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
        try {
            // Create scVars variables;
            generateSCVars();

            // Add variables to model.
            model.update();

            // Set model objective.
            setObjective();

            // Add Constraints
            addMaxCourseSizeConstraint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateSCVars() throws GRBException {
        String s;
        GRBVar var;
        for (int i = 1; i <= studentCount; i++) {
            for (int j = 1; j <= courseCount; j++) {
                s = String.format(FMT_SC, students.get(i).id, courses.get(i).id);
                var = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, s);
                scVars.set(i, j, var);
            }
        }
    }

    private void addMaxCourseSizeConstraint() throws GRBException {
        for (int i = 1; i <= courseCount; i++) {
            GRBLinExpr expr = new GRBLinExpr();
            for (int j = 1; j <= studentCount; j++) {
                expr.addTerm(1, scVars.get(i, j));
            }
            String s = String.format("max_cs_ci_%s", courses.get(i).id);
            model.addConstr(expr, GRB.LESS_EQUAL, MAX_CLASS_SIZE, s);
        }
    }

    private void add() throws GRBException {

    }

    private void setObjective() throws GRBException {
        GRBLinExpr expr = new GRBLinExpr();
        for (int i = 1; i <= studentCount; i++) {
            for (int j = 1; j <= courseCount ; j++) {
                expr.addTerm(1, scVars.get(i,j));
            }
        }
        model.setObjective(expr, GRB.MAXIMIZE);
    }

    // =====================================================================
    // encapsulation of the y-vars array, so that we can make it 1-indexed.​

    private static class SCVars {
        private final GRBVar[][] scVars;

        SCVars(int i, int j) {
            scVars = new GRBVar[i][j];
        }

        void set(int i, int j, GRBVar var) {
            scVars[i - 1][j - 1] = var;
        }

        GRBVar get(int i, int j) {
            return scVars[i - 1][j - 1];
        }
    }
}
