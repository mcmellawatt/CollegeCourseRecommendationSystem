package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.mvc.Controller;

import static controllers.Tags.PAYLOAD;

/**
 * Base class for our controllers, defining some convenience constants
 * and methods.
 */
public abstract class AppController extends Controller {

    /**
     * Creates a response to send back to the UI (no payload).
     *
     * @param user the logged-in username
     * @param responseType the type of response
     * @return the aggregated response data
     */
    protected static ObjectNode createResponse(String user, String responseType) {
        return createResponse(user, responseType, null);
    }

    /**
     * Creates a response to send back to the UI.
     *
     * @param user the logged-in username
     * @param responseType the type of response
     * @param payload the payload data
     * @return the aggregated response data
     */
    protected static ObjectNode createResponse(String user, String responseType,
                                               ObjectNode payload) {
        ObjectNode response = JsonCodec.envelope(user, responseType);
        if (payload != null) {
            response.set(PAYLOAD, payload);
        }
        return response;
    }

}
