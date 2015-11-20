package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Student;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Result;

/**
 * Controller for the history view.
 */
public class HistoryView extends AppController {

    private static final String FULL_NAME = "fullName";

    /**
     * Generates the data required for populating the history view.
     * It is expected that the logged-in user will be presented in the
     * body of the request.
     *
     * @return history view data
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result view() {
        String user = fromRequest(USER);
        Student student = Student.findByUserName(user);

        // TODO: get history data and return that
        ObjectNode payload = objectNode()
                .put(FULL_NAME, student.fullname)
                .put("goo", "zoo");

        Logger.debug("history view page accessed as user '{}'", user);

        return ok(createResponse(user, HISTORY, payload));
    }
}
