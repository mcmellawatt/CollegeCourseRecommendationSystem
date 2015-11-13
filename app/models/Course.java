package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

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
    public String tag;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String abbrev;

    @Constraints.Required
    public boolean core;

    // -- Queries

    private static final Model.Finder<String, Course> FIND =
            new Model.Finder<>(String.class, Course.class);

    /**
     * Retrieve all users.
     */
    public static List<Course> findAll() {
        return FIND.all();
    }

    /**
     * Retrieve a Course from id.
     */
    public static Course findById(String id) {
        return FIND.where().eq("id", id).findUnique();
    }

    // --

    public String toString() {
        return "Course{" + tag + "}";
    }

    public String title() {
        return tag + " " + name;
    }

}