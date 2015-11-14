package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;
import java.util.ArrayList;

/**
 * Student entity managed by Ebean
 */
@Entity
@Table(name="students")
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
    public String fullname = "test";

    @Constraints.Required
    public List<Course> coursesTaken = new ArrayList<Course>();

    // -- Queries

    private static final Model.Finder<String, Student> FIND =
            new Model.Finder<>(String.class, Student.class);

    /**
     * Retrieve all users.
     */
    public static List<Student> findAll() {
        return FIND.all();
    }

    /**
     * Retrieve a Course from id.
     */
    public static Student findById(String id) {
        return FIND.where().eq("id", id).findUnique();
    }

    // --

    public String toString() {
        return "Student{" + fullname + "}";
    }

}