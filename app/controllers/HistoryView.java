package controllers;

import models.Student;
import models.StudentSolution;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Result;

import java.util.List;

import static controllers.JsonCodec.*;
import static controllers.Tags.*;

/**
 * Controller for the history view.
 */
public class HistoryView extends AppController {

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
        Logger.debug("history view page accessed as user '{}'", user);

        List<StudentSolution> solns = StudentSolution.findByStudent(student);
        return ok(createResponse(user, HISTORY,
                                 jsonHistoryViewPayload(student, solns)));
    }

}
