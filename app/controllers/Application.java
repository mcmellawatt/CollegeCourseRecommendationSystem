package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.cache.Cache;
import play.mvc.BodyParser;
import play.mvc.Result;
import views.html.app;
import views.html.login;

import static controllers.JsonCodec.*;
import static controllers.Tags.*;

/**
 * Implements our application controller, responsible for handling user login
 * and the main application view.
 */
public class Application extends AppController {

    private static final Authenticator AUTH = new Authenticator();

    /**
     * Generates the login page.
     *
     * @return login page response
     */
    public static Result login() {
        Logger.debug("login page accessed");
        return ok(login.render());
    }

    /**
     * Handles a login request, returning a JSON response object,
     * defining the next page to load, or (on an invalid login), indicating
     * that the login failed.
     *
     * @return login response
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result doLogin() {
        String user = fromRequest(USER);
        String pass = fromRequest(PASS);
        Logger.debug("got username [{}] and password [{}]", user, pass);

        boolean loginOk = AUTH.authenticate(user, pass);
        ObjectNode response = jsonLoginResponse(user, loginOk);

        if (loginOk) {
            Logger.info("Login for user [{}]", user);

            // add our destination URL
            response.put(REDIRECT, LoginUrls.APP);

            // TODO: intercept admin user and redirect to LoginUrls.ADMIN

            // TODO: is this needed? (Perhaps pass the user in body of "app" post?)
            // remember who the user is
            Cache.set(USER, user);

        } else {
            Logger.warn("Login FAILED for user [{}]", user);
        }

        return ok(response);
    }

    /**
     * Generates the main page for the student application.
     *
     * @return application page content
     */
    public static Result app() {
        String user = Cache.get(USER).toString();
        Logger.debug("app page accessed as user [{}]", user);
        return ok(app.render(user));
    }
}
