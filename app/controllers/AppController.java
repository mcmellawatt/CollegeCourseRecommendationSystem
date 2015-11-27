package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Course;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static controllers.Tags.*;

/**
 * Base class for our controllers, defining some convenience constants
 * and methods.
 */
public class AppController extends Controller {

    private static final String COMMA = ",";

    /**
     * Creates a response to send back to the UI (no payload).
     *
     * @param user the logged-in username
     * @param view the view name
     * @return the aggregated response data
     */
    protected static ObjectNode createResponse(String user, String view) {
        return createResponse(user, view, null);
    }

    /**
     * Creates a response to send back to the UI.
     *
     * @param user the logged-in username
     * @param view the view name
     * @param payload the payload data
     * @return the aggregated response data
     */
    protected static ObjectNode createResponse(String user, String view,
                                               ObjectNode payload) {
        ObjectNode response = JsonCodec.envelope(user, view);
        if (payload != null) {
            response.set(PAYLOAD, payload);
        }
        return response;
    }

    /**
     * Produces a list of tokens from a CSV string.
     *
     * @param csv the string
     * @return a list of tokens
     */
    protected static List<String> fromCsv(String csv) {
        return Arrays.asList(csv.split(COMMA));
    }

    /**
     * Produces a list of courses, in the order defined by the CSV list
     * of course IDs.
     *
     * @param csv CSV of course IDs
     * @return list of courses in the same order
     */
    protected static List<Course> courseListFromCsv(String csv) {
        List<String> ids = fromCsv(csv);
        List<Course> courses = new ArrayList<>();
        for (String id: ids) {
            courses.add(Course.findById(id));
        }
        return courses;
    }

}
