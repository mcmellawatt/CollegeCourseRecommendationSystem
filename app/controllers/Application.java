package controllers;

import models.Course;
import models.Student;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
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
        return ok(
                login.render()
        );
    }

    /**
     * Generates the index page.
     *
     * @return index page response
     */
    public static Result index() {
        return redirect(routes.Application.courses());
    }

    /**
     * Generates the courses page, listing all the available courses.
     *
     * @return courses page response
     */
    public static Result courses() {
        return ok(
                index.render(Course.findAll())
        );
    }

    /**
     * Generates a page with information about the given student.
     *
     * @param id student ID
     * @return student information page
     */
    public static Result getStudent(String id) {
        return ok(
                student.render(Student.findById(id))
        );
    }
}
