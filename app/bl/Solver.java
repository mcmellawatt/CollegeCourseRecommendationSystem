package bl;

import models.Course;
import models.Student;

import java.util.List;
import java.util.Map;

/**
 * Created by Jesse 11/14/15.
 */
public interface Solver {
    Map<Student, List<Course>> solve();
}