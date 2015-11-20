package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Course;
import models.Student;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Result;

/**
 * Controller for the courses view.
 */
public class CoursesView extends AppController {

    private static final String FULL_NAME = "fullName";
    private static final String NUM_COURSES_PREFERRED = "numCoursesPreferred";

    /**
     * Generates the data required for populating the courses view.
     * It is expected that the logged-in user will be presented in the
     * body of the request.
     *
     * @return courses view data
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result view() {
        String user = fromRequest(USER);
        Student student = Student.findByUserName(user);

        // TODO: get courses data and return that
        ObjectNode payload = objectNode()
                .put(FULL_NAME, student.fullname)
                .put(NUM_COURSES_PREFERRED, student.numCoursesPreferred);

        ArrayNode courses = arrayNode();
        for (Course c: student.coursesPreferred) {
            courses.add(json(c));
        }
        payload.set(COURSES, courses);

        Logger.debug("courses view page accessed as user '{}'", user);

        return ok(createResponse(user, COURSES, payload));
    }

    private static ObjectNode json(Course c) {
        return objectNode()
                .put(ID, c.id)
                .put(TAG, c.tag)
                .put(NAME, c.name)
                .put(ABBREV, c.abbrev)
                .put(CORE, c.core);
    }
}
