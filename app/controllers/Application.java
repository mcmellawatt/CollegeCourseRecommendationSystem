package controllers;

import models.Course;
import models.Student;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.courses;
import views.html.login;
import views.html.student;

/**
 * Implements our application controller.
 */
public class Application extends Controller {

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
     * Generates the courses page.
     *
     * @return courses page response
     */
    public static Result index() {
        Logger.debug("index page accessed, redirecting...");
        return redirect(routes.Application.courses());
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
        Logger.debug("student page accessed, for student '{}'", id);
        return ok(
                student.render(Student.findById(id))
        );
    }
}
