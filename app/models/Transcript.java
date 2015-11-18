package models;

import play.data.format.*;
import play.data.validation.*;
import play.db.ebean.*;

import javax.persistence.*;

import java.util.*;

/**
 * Transcript entity managed by Ebean.
 */
@Entity
@Table(name="transcripts")
public class Transcript extends Model {

    private static final String ID = "id";

    @Id
    @Constraints.Required
    @Formats.NonEmpty
    public String id;

    @Constraints.Required
    @OneToOne(mappedBy="transcript")
    public Student student;

    @Constraints.Required
    public int creditsEarned;

    // -- Queries

    private static final Model.Finder<String, Transcript> FIND =
            new Model.Finder<>(String.class, Transcript.class);

    /**
     * Returns all transcripts.
     */
    public static List<Transcript> findAll() {
        return FIND.all();
    }

    /**
     * Returns the transcript with the given ID.
     *
     * @param id transcript ID
     */
    public static Transcript findById(String id) {
        return FIND.where().eq(ID, id).findUnique();
    }

    // --

    public String toString() {
        return "Transcript{" + id + "}";
    }

}