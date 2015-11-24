package models;

import org.springframework.cglib.core.CollectionUtils;
import play.data.format.*;
import play.data.validation.*;
import play.db.ebean.*;

import javax.persistence.*;

import java.util.*;

/**
 * Student entity managed by Ebean.
 */
@Entity
@Table(name="students")
public class Student extends Model {

    private static final String ID = "id";
    private static final String USERNAME = "username";

    private static List<Course> courses = new ArrayList<>();

    @Id
    @Constraints.Required
    @Formats.NonEmpty
    public String id;

    @Constraints.Required
    public String username;

    @Constraints.Required
    public String password;

    @Constraints.Required
    public String fullname;

    @Constraints.Required
    @ManyToMany
    public List<Course> coursesPreferred = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    public List<StudentRequest> studentRequest;

    @Constraints.Required
    public int numCoursesPreferred;

    @Constraints.Required
    @OneToOne
    public Transcript transcript;

    public String courseOrderCsv;

    /**
     * Returns the list of courses for which this student is currently
     * eligible.
     *
     * @return list of eligible courses
     */
    public synchronized List<Course> getEligibleCourses() {
        List<Course> availableCourses = Course.findAll();

        availableCourses.removeAll(transcript.coursesTaken);

        List<Course> missingPreReqs = new ArrayList<>();
        for (Course c : availableCourses) {
            if (!transcript.coursesTaken.containsAll(c.prerequisites)) {
                missingPreReqs.add(c);
            }
        }
        availableCourses.removeAll(missingPreReqs);

        return availableCourses;
    }

    // -- Queries

    private static final Model.Finder<String, Student> FIND =
            new Model.Finder<>(String.class, Student.class);

    /**
     * Returns all registered students.
     *
     * @return all students
     */
    public static List<Student> findAll() {
        return FIND.all();
    }

    /**
     * Returns the student with the given ID.
     *
     * @param id student ID
     * @return corresponding student
     */
    public static Student findById(String id) {

        return FIND.fetch("transcript").where().eq(ID, id).findUnique();
    }

    /**
     * Returns the student with the given username.
     *
     * @param user student username
     * @return corresnponding student
     */
    public static Student findByUserName(String user) {
        return FIND.fetch("transcript").where().eq(USERNAME, user).findUnique();
    }

    // --

    @Override
    public String toString() {
        return "Student{" + id + ":" + username + "}";
    }

}