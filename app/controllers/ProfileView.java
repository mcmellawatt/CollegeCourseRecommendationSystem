package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Student;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Result;

import static controllers.JsonCodec.*;
import static controllers.Tags.*;

/**
 * Controller for the profile view.
 */
public class ProfileView extends AppController {

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
        Logger.debug("profile view page accessed as user '{}'", user);

        ObjectNode payload = jsonProfileViewPayload(student, student.transcript);
        return ok(createResponse(user, PROFILE, payload));
    }
}
