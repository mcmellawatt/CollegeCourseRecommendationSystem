package models;

import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;

/**
 * Course entity managed by Ebean
 */
@Entity
@Table(name="courses")
public class Course extends Model {

    @Id
    @Constraints.Required
    @Formats.NonEmpty
    public String id;

    @Constraints.Required
    public String name;

    // -- Queries

    public static Model.Finder<String, Course> find = new Model.Finder(String.class, Course.class);

    /**
     * Retrieve all users.
     */
    public static List<Course> findAll() {
        return find.all();
    }

    /**
     * Retrieve a Course from id.
     */
    public static Course findById(String id) {
        return find.where().eq("id", id).findUnique();
    }

    // --

    public String toString() {
        return "Course(" + id + ")";
    }

}