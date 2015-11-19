package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.cache.Cache;
import play.mvc.BodyParser;
import play.mvc.Result;
import views.html.app;
import views.html.login;

/**
 * Implements our application controller, responsible for handling user login
 * and the main application view.
 */
public class Application extends AppController {

    private static final String PASS = "pass";
    private static final String GOOD_TO_GO = "goodToGo";
    private static final String REDIRECT = "redirect";
    private static final String APP_URL = "app";

    private static final Authenticator AUTH = new Authenticator();


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
}
