package models;

import play.db.ebean.*;
import javax.persistence.*;
import java.util.*;

/**
 * StudentRequest entity managed by Ebean.
 */
@Entity
@Table(name = "studentrequests")
public class StudentRequest {

    private static final String ID = "id";

    @ManyToOne
    public Student student;

    public Integer sequenceNumber;

    @ManyToMany
    public List<Course> preferredCourses = new ArrayList<>();

    public Integer numCoursesPreferred;

    // -- Queries

    private static final Model.Finder<String, StudentRequest> FIND =
            new Model.Finder<>(String.class, StudentRequest.class);

    /**
     * Returns all available courses.
     */
    public static List<StudentRequest> findAll() {
        return FIND.all();
    }

    /**
     * Returns the course with the given ID.
     *
     * @param id course ID
     */
    public static StudentRequest findById(String id) {
        return FIND.fetch("preferredCourses").fetch("student").where().eq(ID, id).findUnique();
    }

    // --
}
