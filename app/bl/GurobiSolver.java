package bl;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import models.Course;
import models.Student;
import models.StudentRequest;

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

    final int studentCount;
    final int courseCount;

    // Gurobi matrix for students and courses for next semester.
    // This will be the solution.
    private YVars scVars;

    private volatile boolean ready = true;

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

        studentCount = students.size();
        courseCount = courses.size();

        scVars = new YVars(studentCount, courseCount);
    }

    @Override
    public boolean isReady() {
        return ready;
    }


    @Override
    public void initialize() {
        try {
            // Create scVars variables;
            generateSCVars();

            // Add variables to model.
            model.update();

            // Set model objective.
            setObjective();

            // Add Constraints
            addAllCourseSizeConstraints();
            addAllMaxNumOfCoursesConstraints();
            addAllNotEligibleConstraints();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void adjustConstraints(List<StudentRequest> requests) {
        //update the collection of students.
        updateStudentCollection(requests);

        try {
            // update student related constraints and objective.
            updateAllStudentConstraints(requests);
            setObjective();
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Map<Student, List<Course>> solve() {
        ready = false;

        Map<Student, List<Course>> solution = null;
        try {
            //
            model.optimize();

            // format gurobi model solution.
            solution = formatSolution();

        } catch (Exception e) {
            e.printStackTrace();
        }
        ready = true;
        return solution;
    }


    // helper methods from here downwards

    private void generateSCVars() throws GRBException {
        String s;
        GRBVar var;
        for (int i = 0; i < studentCount; i++) {
            for (int j = 0; j < courseCount; j++) {
                s = String.format(FMT_SC, students.get(i).id, courses.get(j).id);
                var = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, s);
                scVars.set(i, j, var);
            }
        }
    }

    // Course size constraints.

    private void addAllCourseSizeConstraints() throws GRBException {
        for (int j = 0; j < courseCount; j++) {
            addCourseSizeConstraint(j);
        }
    }

    private void addCourseSizeConstraint(int j) throws GRBException {
        GRBLinExpr expr = new GRBLinExpr();
        for (int i = 0; i < studentCount; i++) {
            expr.addTerm(1, scVars.get(i, j));
        }
        String s = String.format(FMT_MAX_CS, courses.get(j).id);
        model.addConstr(expr, GRB.LESS_EQUAL, courses.get(j).maxClassSize, s);
    }

    // Number of courses per student constraints.

    private void addAllMaxNumOfCoursesConstraints() throws GRBException {
        for (int i = 0; i < studentCount; i++) {
            addMaxNumOfCoursesConstraint(i);
        }
    }

    private void addMaxNumOfCoursesConstraint(int i) throws GRBException {
        GRBLinExpr expr = new GRBLinExpr();
        for (int j = 0; j < courseCount; j++) {
            expr.addTerm(1, scVars.get(i, j));
        }
        String s = String.format(FMT_MAX_NC, students.get(i).id);
        int num = students.get(i).numCoursesPreferred;
        model.addConstr(expr, GRB.LESS_EQUAL, num, s);
    }

    private void removeMaxNumOfCourseConstraint(int i) throws GRBException {
        String s = String.format(FMT_MAX_NC, students.get(i).id);
        model.remove(model.getConstrByName(s));
    }

    // Not eligible for course constraints.

    private void addAllNotEligibleConstraints() throws GRBException {
        for (int i = 0; i < studentCount; i++) {
            addNotEligibleConstraints(i);
        }
    }

    private void addNotEligibleConstraints(int i) throws GRBException {
        for (int j = 0; j < courseCount; j++) {
            addNotEligibleConstraint(i, j);
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

    // model objective functions.

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

    // general helpers

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

//    private void tester(Map<Student, List<Course>> solution) {
//        for (Student s : students) {
//            System.out.println();
//            System.out.print(s.fullname + " ");
//            for (Course c : solution.get(s)) {
//                System.out.print(" " + c.id + " ");
//            }
//        }
//    }

    private void updateAllStudentConstraints(List<StudentRequest> requests) throws GRBException {
        for (StudentRequest sr : requests) {
            updateStudentConstraints(sr);
        }
    }

    private void updateStudentConstraints(StudentRequest sr) throws GRBException {
        int ndx = findStudentIndex(sr.student);
        removeMaxNumOfCourseConstraint(ndx);
        addMaxNumOfCoursesConstraint(ndx);
    }

    private void updateStudentCollection(List<StudentRequest> requests) {
        for (StudentRequest sr : requests) {
            updateStudent(sr);
        }
    }

    private void updateStudent(StudentRequest sr) {
        int ndx = findStudentIndex(sr.student);
        students.set(ndx, sr.student);
    }

    private int findStudentIndex(Student s) {
        int studentNdx = -1;
        for (int i = 0; i < studentCount; i++) {
            if (students.get(i).id.equals(s.id)) {
                studentNdx = i;
                break;
            }
        }
        return studentNdx;
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
