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
 * StudentRequest entity managed by Ebean.
 */
@Entity
@Table(name = Tables.STUDENTREQUESTS)
public class StudentRequest extends Model {

    /**
     * Internal ID.
     */
    @Id
    @Constraints.Required
    @Formats.NonEmpty
    public Integer id;

    /**
     * Timestamp when record was created.
     */
    @Constraints.Required
    public Long tsCreated = TimeUtils.now();

    /**
     * The solver batch to which this record was submitted.
     */
    @Constraints.Required
    public int batchNumber;

    /**
     * The student who owns this request.
     */
    @Constraints.Required
    @ManyToOne
    public Student student;

    /**
     * The number of courses the student prefers (at the time of the request).
     */
    @Constraints.Required
    public Integer numCoursesPreferred;

    /**
     * The courses preferred by the student (in priority order).
     */
    @Constraints.Required
    @ManyToMany
    public List<Course> coursesPreferred = new ArrayList<>();

    // -- Queries

    private static final Model.Finder<String, StudentRequest> FIND =
            new Model.Finder<>(String.class, StudentRequest.class);

    /**
     * Returns all available student requests.
     *
     * @return all student requests
     */
    public static List<StudentRequest> findAll() {
        return FIND.all();
    }

    /**
     * Returns the student request with the given ID.
     *
     * @param id request ID
     * @return the request
     */
    public static StudentRequest findById(String id) {
        return FIND.fetch(Fields.COURSES_PREFERRED).fetch(Fields.STUDENT)
                .where().eq(Fields.ID, id).findUnique();
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
