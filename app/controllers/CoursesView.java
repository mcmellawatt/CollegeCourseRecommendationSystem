package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Course;
import models.Student;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

        ObjectNode payload = objectNode()
                .put(FULL_NAME, student.fullname)
                .put(NUM_COURSES_PREFERRED, student.numCoursesPreferred);

        if (student.courseOrderCsv != null) {
            payload.put(COURSE_ORDER_CSV, student.courseOrderCsv);
        }

        ArrayNode courses = arrayNode();
        List<Course> eligibleCourses = student.getEligibleCourses();

        for (Course c: eligibleCourses) {
            courses.add(json(c));
        }
        payload.set(COURSES, courses);

        Logger.debug("courses view page accessed as user '{}'", user);

        return ok(createResponse(user, COURSES, payload));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result storeCourseList() {
        String user = fromRequest(USER);
        String csv = fromRequest(COURSE_ORDER_CSV);
        String ncp = fromRequest(NUM_COURSES_PREFERRED);
        Student student = Student.findByUserName(user);

        student.courseOrderCsv = csv;
        student.numCoursesPreferred = Integer.valueOf(ncp);
        Ebean.save(student);

        Logger.debug("storing current course order for user '{}'", user);
        Logger.debug(" as {}", csv);
        Logger.debug(" with num courses preferred as {}", ncp);
        return ok(createResponse(user, ACK));
    }

}
