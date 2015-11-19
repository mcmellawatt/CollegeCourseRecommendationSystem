package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Student;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Result;

/**
 * Controller for profile view.
 */
public class ProfileView extends AppController {
    /**
     * Generates the profile view HTML.
     *
     * @return profile view content
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result view() {
        JsonNode json = request().body().asJson();
        String user = json.findPath(USER).textValue();
        Student student = Student.findByUserName(user);

        Logger.debug("profile view page accessed as user '{}'", user);
        ObjectNode response = objectNode()
                .put(USER, user)
                .put(VIEW, "profile");

        ObjectNode studentNode = objectNode()
                .put("fullName", student.fullname)
                .put("numCoursesPreferred", student.numCoursesPreferred);
        response.set("student", studentNode);

        return ok(response);
    }
}
