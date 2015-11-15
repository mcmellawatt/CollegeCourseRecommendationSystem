package bl;

import gurobi.*;
import models.Course;
import models.Student;

import java.util.List;

/**
 * Created by Jesse on 11/12/15.
 */
public class GurobiSolver implements Solver {
    private GRBModel model;

    // Currently '100' is just a place holder, the actual value for
    // maxClassSize will come from some business logic class. Ideally each
    // class would have a max class size, this can be addressed later.
    private int maxClaseSize = 100;

    // List of Student objects.
    private List<Student> students;

    //List of available Courses objects.
    private List<Course> courses;


    /**
     * Initialize the Gurobi model.
     * Initialize List of Student objects.
     * Initialize CourseCatalog.
     */
    public GurobiSolver() {

        // Init model.
        GRBEnv env;
        try {
            env = new GRBEnv();
            GRBModel model = new GRBModel(env);
        } catch (GRBException e) {
            e.printStackTrace();
        }

        // Init Student List.
        students = Student.findAll();

        //Init Course List.
        courses = Course.findAll();
    }


    @Override
    public void optimize() {

        // Number of students
        int studentTotal = students.size();

        // Number of courses
        int courseTotal = courses.size();

        try {
            // Gurobi matrix for students and courses for next semester.
            // This will be the solution.
            GRBVar[][] sc = new GRBVar[studentTotal][courseTotal];

            // Create GRBVar[][] sc variables;
            for (int i = 0; i < studentTotal; i++) {
                for (int j = 0; j < courseTotal; j++) {
                    String st = "si_%s_ci_%s";
                    st = String.format(st, students.get(i).id,
                            courses.get(i).id);

                    sc[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, st);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
