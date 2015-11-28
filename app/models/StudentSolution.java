package models;

import bl.TimeUtils;
import models.names.Fields;
import models.names.Tables;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentSolution entity managed by EBean.
 */
@Entity
@Table(name = Tables.STUDENTSOLUTIONS)
public class StudentSolution extends Model
        implements Comparable<StudentSolution> {

    /**
     * Internal ID.
     */
    @Id
    @Constraints.Required
    @Formats.NonEmpty
    public String id;

    /**
     * Timestamp when record was created.
     */
    @Constraints.Required
    public Long tsCreated = TimeUtils.now();

    /**
     * Solver batch number.
     */
    @Constraints.Required
    public int batchNumber;

    /**
     * The student who owns this solution.
     */
    @Constraints.Required
    @ManyToOne
    public Student student;

    /**
     * The number of courses the student prefers (at the time of the request).
     */
    @Constraints.Required
    public int numCoursesPreferred;

    /**
     * True if this record was not explicitly requested by the student. That
     * is, if the record was generated as the result of _other_ student(s)
     * submitting requests.
     */
    @Constraints.Required
    public boolean derived;

    /**
     * The courses that the solver recommends for this student, based on
     * entered preferences.
     */
    @Constraints.Required
    @ManyToMany
    public List<Course> recommendedCourses = new ArrayList<>();

    // -- Queries

    private static final Model.Finder<String, StudentSolution> FIND =
            new Model.Finder<>(String.class, StudentSolution.class);

    /**
     * Returns all available student solutions.
     *
     * @return all student solutions
     */
    public static List<StudentSolution> findAll() {
        return FIND.all();
    }

    /**
     * Returns the student solution with the given ID.
     *
     * @param id solution ID
     * @return the solution
     */
    public static StudentSolution findById(String id) {
        return FIND.fetch(Fields.RECOMMENDED_COURSES).fetch(Fields.STUDENT)
                .where().eq(Fields.ID, id).findUnique();
    }

    /**
     * Returns the set of solutions for a given student.
     *
     * @param student student
     * @return solutions for that student
     */
     public static List<StudentSolution> findByStudent(Student student) {
         return FIND.fetch(Fields.RECOMMENDED_COURSES).fetch(Fields.STUDENT)
                 .where().eq(Fields.STUDENT, student).findList();
     }

    /**
     * Returns the solution for a given student and batch number.
     *
     * @param student student
     * @param batch batch number
     * @return matching solution for that student, if any
     */
     public static StudentSolution findByStudent(Student student, int batch) {
         return FIND.fetch(Fields.RECOMMENDED_COURSES).fetch(Fields.STUDENT)
                 .where().eq(Fields.STUDENT, student)
                         .eq(Fields.BATCH_NUMBER, batch).findUnique();
     }

    // --

    /**
     * Return the record created timestamp as a display string.
     *
     * @return created timestamp
     */
    public String created() {
        return TimeUtils.toDisplayString(tsCreated);
    }

    /**
     * Returns true if the specified solution has the same student,
     * number of courses preferred and recommended courses list, as this
     * solution.
     *
     * @param other solution to compare against
     * @return true if these solutions are equivalent, false otherwise
     */
    public boolean sameSolution(StudentSolution other) {
        if (other == null) return false;
        return student.equals(other.student) &&
                numCoursesPreferred == other.numCoursesPreferred &&
                recommendedCourses.equals(other.recommendedCourses);
    }

    /**
     * Comparable implemented to return solutions in reverse chronological
     * order.
     *
     * @param o other to compare to
     * @return positive, zero, negative as this is bigger, same, smaller than other
     */
    @Override
    public int compareTo(StudentSolution o) {
        Long me = tsCreated;
        Long you = o.tsCreated;
        return me.compareTo(you);
    }
}
