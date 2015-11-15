package models;

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

    @Id
    @Constraints.Required
    @Formats.NonEmpty
    public String id;

    @Constraints.Required
    public String username;

    @Constraints.Required
    public String password;

    @Constraints.Required
    public String fullname = "test";

    @ManyToMany
    @Constraints.Required
    public List<Course> coursesTaken = new ArrayList<>();

    // -- Queries

    private static final Model.Finder<String, Student> FIND =
            new Model.Finder<>(String.class, Student.class);

    /**
     * Returns all students.
     */
    public static List<Student> findAll() {
        return FIND.all();
    }

    /**
     * Returns the student with the given ID.
     *
     * @param id student ID
     */
    public static Student findById(String id) {
        return FIND.where().eq(ID, id).findUnique();
    }

    // --

    public String toString() {
        return "Student{" + fullname + "}";
    }

}