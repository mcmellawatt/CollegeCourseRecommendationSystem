package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Student entity managed by Ebean.
 */
@Entity
@Table(name = Tables.STUDENTS)
public class Student extends Model {

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

    @OneToMany(mappedBy = Fields.STUDENT)
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

        return FIND.fetch(Fields.TRANSCRIPT)
                .where().eq(Fields.ID, id).findUnique();
    }

    /**
     * Returns the student with the given username.
     *
     * @param user student username
     * @return corresnponding student
     */
    public static Student findByUserName(String user) {
        return FIND.fetch(Fields.TRANSCRIPT)
                .where().eq(Fields.USERNAME, user).findUnique();
    }

    // --

    @Override
    public String toString() {
        return "Student{" + id + ":" + username + "}";
    }

}