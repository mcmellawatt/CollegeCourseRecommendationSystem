package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Course;
import models.Student;
import play.Logger;
import play.cache.Cache;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.app;
import views.html.courses;
import views.html.login;
import views.html.student;

/**
 * Implements our application controller.
 */
public class Application extends Controller {

    private static final String USER = "user";
    private static final String PASS = "pass";
    private static final String GOOD_TO_GO = "goodToGo";
    private static final String REDIRECT = "redirect";
    private static final String APP_URL = "app";

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Authenticator AUTH = new Authenticator();

    private static ObjectNode objectNode() {
        return MAPPER.createObjectNode();
    }

    private static ArrayNode arrayNode() {
        return MAPPER.createArrayNode();
    }


    /**
     * Generates the login page.
     *
     * @return login page response
     */
    public static Result login() {
        Logger.debug("login page accessed");
        return ok(
                login.render()
        );
    }

    /**
     * Handles login request, returning a JSON response object, telling the
     * client what page to load next.
     *
     * @return login response
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result doLogin() {
        JsonNode json = request().body().asJson();
        String user = json.findPath(USER).textValue();
        String pass = json.findPath(PASS).textValue();

        Logger.debug("got username [{}] and password [{}]", user, pass);

        ObjectNode response = objectNode()
                .put(USER, user);

        if (AUTH.authenticate(user, pass)) {
            Logger.info("Login for user [{}]", user);

            // remember who the user is
            Cache.set(USER, user);

            // tell the login page that we are good to proceed
            response.put(GOOD_TO_GO, true)
                    .put(REDIRECT, APP_URL);

        } else {
            Logger.warn("Login FAILED for user [{}]", user);
            response.put(GOOD_TO_GO, false);
        }

        return ok(response);
    }


    /**
     * Generates the main page for our single-page-application.
     *
     * @return application page content
     */
    public static Result app() {
        String user = Cache.get(USER).toString();
        Logger.debug("app page accessed as user [{}]", user);
        return ok(
                app.render(user)
        );
    }

    /**
     * Generates the courses page, listing all the available courses.
     *
     * @return courses page response
     */
    public static Result courses() {
        Logger.debug("courses page accessed");
        return ok(
                courses.render(Course.findAll())
        );
    }

    /**
     * Generates a page with information about the given student.
     *
     * @param id student ID
     * @return student information page
     */
    public static Result getStudent(String id) {
        Logger.debug("student page accessed, for student-id '{}'", id);
        return ok(
                student.render(Student.findById(id))
        );
    }
}
