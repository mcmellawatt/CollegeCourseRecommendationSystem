package controllers;

import bl.SchedulerService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Course;
import models.Student;
import models.StudentRequest;
import models.StudentSolution;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static controllers.JsonCodec.*;
import static controllers.Tags.*;

/**
 * Controller for the courses view.
 */
public class CoursesView extends AppController {

    private static final String COMMA = ",";

    private static List<Course> courseListFromCsv(String csv) {
        List<String> ids = Arrays.asList(csv.split(COMMA));
        List<Course> courses = new ArrayList<>();
        for (String id: ids) {
            courses.add(Course.findById(id));
        }
        return courses;
    }

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
        Logger.debug("courses view page accessed as user '{}'", user);
        return ok(createResponse(user, COURSES, jsonCoursesViewPayload(student)));
    }

    private static Student updateStudent(boolean isRequest) {
        String user = fromRequest(USER);
        String csv = fromRequest(COURSE_ORDER_CSV);
        int numCP = fromRequestInt(NUM_COURSES_PREFERRED);

        Student student = Student.findByUserName(user);
        student.courseOrderCsv = csv;
        student.numCoursesPreferred = numCP;

        if (isRequest) {
            student.coursesPreferred.clear();
            student.coursesPreferred.addAll(courseListFromCsv(csv));
        }
        student.touch();
        student.save();

        String msg = isRequest ? "SUBMITTING REQUEST" : "Saving Order";
        Logger.debug("{} for user '{}'", msg, user);
        Logger.debug(" as {}", csv);
        Logger.debug(" with num courses preferred as {}", numCP);
        return student;
    }

    private static StudentRequest createRequest(Student student) {
        StudentRequest sr = new StudentRequest();
        sr.coursesPreferred.addAll(student.coursesPreferred);
        sr.numCoursesPreferred = student.numCoursesPreferred;
        sr.student = student;
        sr.save();
        return sr;
    }

    /**
     * Updates the student record with the prioritized list of courses.
     *
     * @return stored acknowledgement
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result storeCourseList() {
        Student student = updateStudent(false);
        return ok(createResponse(student.username, ACK));
    }

    /**
     * Updates the student record with prioritized list of courses, and
     * submits a recommendation request to the scheduler, for processing.
     *
     * @return submitted acknowledgement
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result submitRequest() {
        Student student = updateStudent(true);
        StudentRequest sr = createRequest(student);

        int batch = SchedulerService.SINGLETON.submitRequest(sr);
        student.waitingForBatch = batch;
        student.save();
        Logger.debug(" --now queued up for batch {}", batch);

        ObjectNode payload = jsonBatchPayload(batch);
        return ok(createResponse(student.username, SUBMITTED, payload));
    }

    /**
     * Polls to see if the solution for which the student is waiting has been
     * persisted to the database yet. If it has, the results are returned to
     * the UI to be displayed.
     *
     * @return results nack, or ack with solution details
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result pollRequest() {
        String user = fromRequest(USER);
        int batch = fromRequestInt(BATCH);
        Logger.debug("Polling for solution: u={}, b={}", user, batch);

        Student student = Student.findByUserName(user);
        StudentSolution soln = StudentSolution.findByStudent(student, batch);
        if (soln != null) {
            student.waitingForBatch = 0;
            student.save();
            return ok(createResponse(user, RESULTS, jsonSolutionResult(soln)));
        }
        return ok(createResponse(user, RESULTS));
    }
}
