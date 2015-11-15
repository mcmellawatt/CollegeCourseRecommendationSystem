package models;

import play.data.format.*;
import play.data.validation.*;
import play.db.ebean.*;

import javax.persistence.*;

import java.util.*;

/**
 * Course entity managed by Ebean.
 */
@Entity
@Table(name="courses")
public class Course extends Model {

    private static final String ID = "id";

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
     * Returns all available courses.
     */
    public static List<Course> findAll() {
        return FIND.all();
    }

    /**
     * Returns the course with the given ID.
     *
     * @param id course ID
     */
    public static Course findById(String id) {
        return FIND.where().eq(ID, id).findUnique();
    }

    // --

    @Override
    public String toString() {
        return "Course{" + tag + "}";
    }

    /**
     * Returns the course title, a concatenation of the course tag and the
     * course name.
     *
     * @return course title
     */
    public String title() {
        return tag + " " + name;
    }

}