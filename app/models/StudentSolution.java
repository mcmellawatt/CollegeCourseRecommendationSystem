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
public class StudentSolution extends Model {

    @Id
    @Constraints.Required
    @Formats.NonEmpty
    public String id;

    @Constraints.Required
    public Long tsCreated = TimeUtils.now();

    @Constraints.Required
    public int batchNumber;

    @Constraints.Required
    @ManyToOne
    public Student student;

    @Constraints.Required
    public int numCoursesPreferred;

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
}
