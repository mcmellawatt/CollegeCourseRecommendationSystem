package controllers;

import bl.SchedulerService;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Course;
import models.Student;
import models.StudentRequest;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the courses view.
 */
public class CoursesView extends AppController {

    private static final String FULL_NAME = "fullName";
    private static final String NUM_COURSES_PREFERRED = "numCoursesPreferred";
    private static final String BATCH = "batch";

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

    private static List<Course> courseListFromCsv(String csv) {
        List<String> ids = fromCsv(csv);
        List<Course> courses = new ArrayList<>();
        for (String id: ids) {
            courses.add(Course.findById(id));
        }
        return courses;
    }

    private static Student updateStudent(boolean isRequest) {
        String user = fromRequest(USER);
        String csv = fromRequest(COURSE_ORDER_CSV);
        String ncp = fromRequest(NUM_COURSES_PREFERRED);
        final int numCP = Integer.valueOf(ncp);

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
        Logger.debug(" with num courses preferred as {}", ncp);
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

    @BodyParser.Of(BodyParser.Json.class)
    public static Result storeCourseList() {
        Student student = updateStudent(false);
        return ok(createResponse(student.username, ACK));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result submitRequest() {
        Student student = updateStudent(true);
        StudentRequest sr = createRequest(student);

        int batch = SchedulerService.SINGLETON.submitRequest(sr);
        Logger.debug(" :: now queued up for batch {}", batch);

        ObjectNode payload = objectNode().put(BATCH, batch);
        return ok(createResponse(student.username, SUBMITTED, payload));
    }
}
