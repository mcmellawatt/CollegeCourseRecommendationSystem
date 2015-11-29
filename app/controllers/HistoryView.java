package controllers;

import models.Student;
import models.StudentSolution;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

        List<StudentSolution> solutions =
                filterSolutions(StudentSolution.findByStudent(student));

        return ok(createResponse(user, HISTORY,
                                 jsonHistoryViewPayload(student, solutions)));
    }

    private static List<StudentSolution> filterSolutions(List<StudentSolution> ssRaw) {
        /*
            (1) Sort the solutions into chronological order
            (2) Find the first non-derived solution, add it to the list
            (3) For every subsequent solution:
                (a) if non-derived, add it to the list
                (b) if derived and not same solution as previous, add to list
            (4) Reverse the list (to reverse-chronological order)
         */

        final int nRaw = ssRaw.size();
        StudentSolution[] array = ssRaw.toArray(new StudentSolution[nRaw]);
        Arrays.sort(array);

        List<StudentSolution> filtered = new ArrayList<>();
        StudentSolution previous = null;
        int index = 0;

        while (index < nRaw && previous == null) {
            StudentSolution solution = array[index++];
            if (!solution.derived) {
                filtered.add(solution);
                previous = solution;
            }
        }

        while (index < nRaw) {
            StudentSolution solution = array[index++];
            if (!solution.derived || !solution.sameSolution(previous)) {
                filtered.add(solution);
                previous = solution;
            }
        }

        Collections.reverse(filtered);
        Logger.debug("  solutions: {} raw, {} filtered", nRaw, filtered.size());
        return filtered;
    }

}
