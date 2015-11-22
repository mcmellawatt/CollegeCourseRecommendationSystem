package bl;

import models.Course;
import models.Student;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Jesse 11/14/15.
 */
public interface Solver {
    LinkedHashMap<Student, ArrayList<Course>> solve();
}