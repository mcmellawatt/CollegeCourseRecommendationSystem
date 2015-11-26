package models;

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
    public int batchNumber;

    @Constraints.Required
    @ManyToOne
    public Student student;

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
     * @param s student
     * @return solutions for that student
     */
     public static List<StudentSolution> findByStudent(Student s) {
         return FIND.fetch(Fields.RECOMMENDED_COURSES).fetch(Fields.STUDENT)
                 .where().eq(Fields.STUDENT, s).findList();
     }

}
