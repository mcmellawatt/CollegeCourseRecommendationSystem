package controllers;

import models.*;
import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return redirect(routes.Application.courses());
    }

    public static Result courses() {
        return ok(
                index.render(Course.findAll())
        );
    }

    public static Result getStudent(String id) {
        return ok(
                student.render(Student.findById(id))
        );
    }
}

