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

    public static final Integer CREDITVALUE = 3;

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

    @Constraints.Required
    public Integer maxClassSize;

    @ManyToMany
    public List<Transcript> transcriptsIncludingCourse = new ArrayList<>();

    //This is a list of courses that this course is a prerequisite for
    @ManyToMany
    @JoinTable(name = "courses_prerequisites", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "prerequisite_id"))
    public List<Course> prerequisiteFor = new ArrayList<>();

    //This is a list of prerequisites required to take this course
    @ManyToMany(mappedBy="prerequisiteFor")
    public List<Course> prerequisites = new ArrayList<>();

    @Constraints.Required
    @ManyToMany(mappedBy="coursesPreferred")
    public List<Student> studentsInterested = new ArrayList<>();

    @ManyToMany(mappedBy="coursesPreferred")
    public List<StudentRequest> studentRequest = new ArrayList<>();

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
        return FIND.fetch("studentsInterested").where().eq(ID, id).findUnique();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Course course = (Course) o;

        if (!id.equals(course.id)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}