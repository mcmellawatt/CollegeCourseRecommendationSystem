package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Transcript entity managed by Ebean.
 *
 * Associates a student with the courses they have taken.
 */
@Entity
@Table(name = Tables.TRANSCRIPTS)
public class Transcript extends Model {

    @Id
    @Constraints.Required
    @Formats.NonEmpty
    public String id;

    @Constraints.Required
    @OneToOne(mappedBy = Fields.TRANSCRIPT)
    public Student student;

    @ManyToMany(mappedBy = Fields.TRANSCRIPTS_INCLUDING_COURSE)
    public List<Course> coursesTaken = new ArrayList<>();

    /**
     * Returns the number of credits earned for courses taken in this
     * transcript.
     *
     * @return number of credits earned
     */
    public int getCreditsEarned() {
        return coursesTaken.size() * Course.CREDIT_VALUE;
    }

    // -- Queries

    private static final Model.Finder<String, Transcript> FIND =
            new Model.Finder<>(String.class, Transcript.class);

    /**
     * Returns all transcripts.
     *
     * @return all transcripts
     */
    public static List<Transcript> findAll() {
        return FIND.all();
    }

    /**
     * Returns the transcript with the given ID.
     *
     * @param id transcript ID
     * @return transcript
     */
    public static Transcript findById(String id) {
        return FIND.fetch(Fields.STUDENT).fetch(Fields.COURSES_TAKEN)
                .where().eq(Fields.ID, id).findUnique();
    }

    // --

    public String toString() {
        return "Transcript{" + id + "}";
    }

}