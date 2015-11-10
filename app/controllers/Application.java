package controllers;

import models.Course;
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
}

