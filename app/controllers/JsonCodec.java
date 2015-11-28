package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Course;
import models.Student;
import models.StudentSolution;
import models.Transcript;
import play.mvc.Controller;

import java.util.List;

import static controllers.Tags.*;

/**
 * Provides encoding and decoding of model object structures into/from JSON
 * structures.
 */
public class JsonCodec {

    private static final ObjectMapper MAPPER = new ObjectMapper();


    // == DECODE JSON ==

    private static JsonNode nodeFromRequest(String key) {
        return Controller.request().body().asJson().findPath(key);
    }

    /**
     * Returns the value from the request body for the given key.
     * It is assumed that the body is formatted JSON, the key/value pair is
     * at the root level, and the value is a string.
     *
     * @param key the key
     * @return the string value for the given key
     */
    static String fromRequest(String key) {
        return nodeFromRequest(key).textValue();
    }

    /**
     * Returns the value from the request body for the given key.
     * It is assumed that the body is formatted JSON, the key/value pair is
     * at the root level, and the value is an integer.
     *
     * @param key the key
     * @return the integer value for the given key
     */
    static int fromRequestInt(String key) {
        return nodeFromRequest(key).asInt();
    }

    /**
     * Returns the array from the request body for the given key.
     * It is assumed that the body is formatted JSON, the key/value pair is
     * at the root level, and the value is an array.
     *
     * @param key the key
     * @return the array for the given key
     */
    static ArrayNode arrayFromRequest(String key) {
        JsonNode json = Controller.request().body().asJson();
        return (ArrayNode) json.withArray(key);
    }


    // == ENCODE JSON ==

    /**
     * Creates a freshly minted object node.
     *
     * @return an empty object node
     */
    private static ObjectNode objectNode() {
        return MAPPER.createObjectNode();
    }

    /**
     * Creates a freshly minted array node.
     *
     * @return an empty array node
     */
    private static ArrayNode arrayNode() {
        return MAPPER.createArrayNode();
    }

    /**
     * Returns a pre-populated outer envelope for a response back to the client.
     *
     * @param user username
     * @param responseType response type
     * @return JSON node representation
     */
    static ObjectNode envelope(String user, String responseType) {
        return JsonCodec.objectNode()
                .put(USER, user)
                .put(RESPONSE_TYPE, responseType);
    }

    /**
     * Produces a JSON representation of the specified student.
     *
     * @param student the student
     * @return JSON object node representation
     */
    static ObjectNode json(Student student) {
        ObjectNode node = objectNode()
                .put(USER, student.username)
                .put(FULL_NAME, student.fullname)
                .put(BATCH, student.waitingForBatch)
                .put(NUM_COURSES_PREFERRED, student.numCoursesPreferred);

        if (student.courseOrderCsv != null) {
            node.put(COURSE_ORDER_CSV, student.courseOrderCsv);
        }

        return node;
    }

    /**
     * Produces a JSON representation of the specified transcript.
     *
     * @param trans the transcript
     * @return JSON object node representation
     */
    static ObjectNode json(Transcript trans) {
        ObjectNode node = objectNode()
                .put(CREDITS_EARNED, trans.getCreditsEarned());
        node.set(COURSES_TAKEN, jsonCourseList(trans.coursesTaken));
        return node;
    }

    /**
     * Produces a JSON representation of the specified course.
     *
     * @param course the course
     * @return JSON object node representation
     */
    static ObjectNode json(Course course) {
        return objectNode()
                .put(ID, course.id)
                .put(TAG, course.tag)
                .put(NAME, course.name)
                .put(ABBREV, course.abbrev)
                .put(CORE, course.core);
    }

    /**
     * Produces a JSON representation of the specified student solution.
     *
     * @param solution the solution
     * @return JSON object node representation
     */
    static ObjectNode json(StudentSolution solution) {
        ObjectNode node = objectNode()
                .put(BATCH, solution.batchNumber)
                .put(TIMESTAMP, solution.created())
                .put(NUM_COURSES_PREFERRED, solution.numCoursesPreferred);
        node.set(RECOMMENDED, jsonCourseList(solution.recommendedCourses));
        return node;
    }

    /**
     * Produces a JSON representation of the specified list of courses.
     *
     * @param courses the courses
     * @return JSON array node representation
     */
    static ArrayNode jsonCourseList(List<Course> courses) {
        ArrayNode array = arrayNode();
        for (Course c: courses) {
            array.add(json(c));
        }
        return array;
    }

    /**
     * Produces a JSON representation of the specified list of Student solutions.
     *
     * @param solutions the solutions
     * @return JSON array node representation
     */
    static ArrayNode jsonSolutionList(List<StudentSolution> solutions) {
        ArrayNode array = arrayNode();
        for (StudentSolution ss: solutions) {
            array.add(json(ss));
        }
        return array;
    }


    // === Custom Payloads

    /**
     * Produces a JSON representation of the payload data for a login response.
     *
     * @param user the user attempting the login
     * @param loginOk indicating success of login
     * @return JSON object node representation
     */
    static ObjectNode jsonLoginResponse(String user, boolean loginOk) {
        return objectNode()
                .put(USER, user)
                .put(GOOD_TO_GO, loginOk);
    }

    /**
     * Produces a JSON representation of the payload data that needs to be
     * returned to the profile view.
     *
     * @param student the student to which the view pertains
     * @param transcript the student's transcript
     * @return JSON representation of the profile data
     */
    static ObjectNode jsonProfileViewPayload(Student student,
                                             Transcript transcript) {
        ObjectNode payload = objectNode();
        payload.set(STUDENT, json(student));
        payload.set(TRANSCRIPT, json(transcript));
        return payload;
    }

    /**
     * Produces a JSON representation of the payload data that needs to be
     * returned to the courses view.
     *
     * @param student the student to which the view pertains
     * @return JSON representation of the courses data
     */
    static ObjectNode jsonCoursesViewPayload(Student student) {
        ObjectNode payload = objectNode();
        payload.set(STUDENT, json(student));
        payload.set(COURSES, jsonCourseList(student.getEligibleCourses()));
        return payload;
    }

    /**
     * Returns a JSON representation encapsulating a batch number.
     *
     * @param batchNumber the batch number
     * @return JSON representation of payload
     */
    static ObjectNode jsonBatchPayload(int batchNumber) {
        return objectNode().put(BATCH, batchNumber);
    }

    /**
     * Produces a JSON representation of the solution result that a student
     * coures view is wanting to display.
     *
     * @param solution the solution
     * @return JSON object node representation
     */
    static ObjectNode jsonSolutionResult(StudentSolution solution) {
        ObjectNode payload = objectNode().put(BATCH, solution.batchNumber);
        payload.set(RECOMMENDED, jsonCourseList(solution.recommendedCourses));
        return payload;
    }

    /**
     * Produces a JSON representation of the payload data that needs to be
     * returned to the history view.
     *
     * @param student the student to which the view pertains
     * @return JSON representation of the history data
     */
    static ObjectNode jsonHistoryViewPayload(Student student,
                                             List<StudentSolution> solutions) {
        ObjectNode payload = objectNode();
        payload.set(STUDENT, json(student));
        payload.set(SOLUTIONS, jsonSolutionList(solutions));
        return payload;
    }
}
