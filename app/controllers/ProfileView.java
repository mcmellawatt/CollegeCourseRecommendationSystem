package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Course;
import models.Student;
import models.Transcript;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Result;

/**
 * Controller for the profile view.
 */
public class ProfileView extends AppController {

    private static final String FULL_NAME = "fullName";
    private static final String NUM_COURSES_PREFERRED = "numCoursesPreferred";
    private static final String COURSES_TAKEN = "coursesTaken";
    private static final String CREDITS_EARNED = "creditsEarned";

    /**
     * Generates the data required for populating the profile view.
     * It is expected that the logged-in user will be presented in the
     * body of the request.
     *
     * @return profile view data
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result view() {
        String user = fromRequest(USER);
        Student student = Student.findByUserName(user);
        ObjectNode payload = objectNode()
                .put(FULL_NAME, student.fullname)
                .put(NUM_COURSES_PREFERRED, student.numCoursesPreferred);

        Transcript transcript = student.transcript;
        ArrayNode taken = arrayNode();
        for (Course c: transcript.coursesTaken) {
            taken.add(json(c));
        }
        payload.set(COURSES_TAKEN, taken);
        payload.put(CREDITS_EARNED, transcript.getCreditsEarned());

        Logger.debug("profile view page accessed as user '{}'", user);

        return ok(createResponse(user, PROFILE, payload));
    }
}
