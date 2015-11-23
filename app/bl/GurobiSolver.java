package bl;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import models.Course;
import models.Student;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Our ILP Solver implementation, using the Gurobi library.
 */
public class GurobiSolver implements Solver {

    // Currently '30' is just a place holder, the actual value for
    // maxClassSize will come from some business logic class. Ideally each
    // class would have a max class size, this can be addressed later.

    private static final String FMT_SC = "si_%s_ci_%s";
    private static final String FMT_MAX_CS = "max_cs_ci_%s";
    private static final String FMT_MAX_NC = "max_nc_si_%s";
    private static final String FMT_NE_SC = "ne_si_%s_ci_%s";

    private GRBModel model;
    private List<Student> students;
    private List<Course> courses;

    final int studentCount = students.size();
    final int courseCount = courses.size();

    // Gurobi matrix for students and courses for next semester.
    // This will be the solution.
    private YVars scVars = new YVars(studentCount, courseCount);

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
    public Map<Student, List<Course>> solve() {
        Map<Student, List<Course>> solution = null;
        try {
            // Create scVars variables;
            generateSCVars();

            // Add variables to model.
            model.update();

            // Set model objective.
            setObjective();

            // Add Constraints
            addMaxCourseSizeConstraints();
            addMaxNumCoursesConstraint();
            addAllNotEligibleConstraints();

            // optimize model.
            model.optimize();

            // format gurobi model solution.
            solution = formatSolution();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return solution;
    }

    private void generateSCVars() throws GRBException {
        String s;
        GRBVar var;
        for (int i = 0; i < studentCount; i++) {
            for (int j = 0; j < courseCount; j++) {
                s = String.format(FMT_SC, students.get(i).id, courses.get(i).id);
                var = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, s);
                scVars.set(i, j, var);
            }
        }
    }

    private void addMaxCourseSizeConstraints() throws GRBException {
        for (int j = 0; j < courseCount; j++) {
            addMaxCourseSizeConstraint(j);
        }
    }

    private void addMaxCourseSizeConstraint(int j) throws GRBException {
        GRBLinExpr expr = new GRBLinExpr();
        for (int i = 0; i < studentCount; i++) {
            expr.addTerm(1, scVars.get(i, j));
        }
        String s = String.format(FMT_MAX_CS, courses.get(j).id);
        model.addConstr(expr, GRB.LESS_EQUAL, courses.get(j).maxClassSize, s);
    }

    private void addMaxNumCoursesConstraint() throws GRBException {
        for (int i = 0; i < studentCount; i++) {
            GRBLinExpr expr = new GRBLinExpr();
            for (int j = 0; j < courseCount; j++) {
                expr.addTerm(1, scVars.get(i, j));
            }
            String s = String.format(FMT_MAX_NC, students.get(i).id);
            int num = students.get(i).numCoursesPreferred;
            model.addConstr(expr, GRB.LESS_EQUAL, num, s);
        }
    }

    private void addAllNotEligibleConstraints() throws GRBException {
        for (int i = 0; i < studentCount; i++) {
            for (int j = 0; j < courseCount; j++) {
                addNotEligibleConstraint(i, j);
            }
        }
    }

    private void addNotEligibleConstraint(int i, int j) throws GRBException {
        if (!isStudentEligibleForCourse(students.get(i), courses.get(j))) {
            GRBLinExpr expr = new GRBLinExpr();
            expr.addTerm(1, scVars.get(i, j));
            String s = String.format(FMT_NE_SC, students.get(i), courses.get(j).id);
            model.addConstr(expr, GRB.LESS_EQUAL, 0, s);
        }
    }

    private void setObjective() throws GRBException {
        GRBLinExpr expr = new GRBLinExpr();
        for (int i = 0; i < studentCount; i++) {
            for (int j = 0; j < courseCount; j++) {
                double c = Coefficient.get(students.get(i), courses.get(j));
                expr.addTerm(c, scVars.get(i, j));
            }
        }
        model.setObjective(expr, GRB.MAXIMIZE);
    }

    private boolean isStudentEligibleForCourse(Student s, Course c) {
        for (Course ec : s.getEligibleCourses()) {
            if (c.id.equals(ec.id)) {
                return true;
            }
        }
        return false;
    }

    private Map<Student, List<Course>> formatSolution() throws GRBException {
        Map<Student, List<Course>> solution = new LinkedHashMap<>();
        for (int i = 0; i < studentCount; i++) {
            ArrayList<Course> recCourses = new ArrayList<>();
            for (int j = 0; j < courseCount; j++) {
                double value = scVars.get(i, j).get(GRB.DoubleAttr.X);
                if (value == 1) {
                    recCourses.add(courses.get(j));
                }
            }
            solution.put(students.get(i), recCourses);
        }
        return solution;
    }

    // =====================================================================
    // encapsulation of the y-vars array.​

    private static class YVars {
        private final GRBVar[][] yVars;

        YVars(int i, int j) {
            yVars = new GRBVar[i][j];
        }

        void set(int i, int j, GRBVar var) {
            yVars[i][j] = var;
        }

        GRBVar get(int i, int j) {
            return yVars[i][j];
        }
    }

    // =====================================================================
    // modifier class used for defining coefficients in model objective.​

    private static class Coefficient {

        private static final double NUM_COURSES = Course.findAll().size();
        private static final double NUM_CREDITS_REQ = 30;
        private static final double pScale = .01;
        private static final double sScale = .99;

        static double get(Student s, Course c) {
            double seniority = s.transcript.getCreditsEarned();
            double priority = getPriorityLevel(s, c);
            return sScale * normalizeSeniority(seniority) + pScale * priority;
        }

        private static double normalizeSeniority(double credits) {
            return credits * (NUM_COURSES / NUM_CREDITS_REQ);
        }

        private static double getPriorityLevel(Student s, Course c) {
            double priority = 0;
            for (int i = 0; i < s.coursesPreferred.size(); i++) {
                if ((s.coursesPreferred.get(i).id).equals(c.id)) {
                    priority = NUM_COURSES - i;
                    break;
                }
            }
            return priority;
        }
    }
}
