package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for our controllers, defining some convenience constants
 * and methods.
 */
public class AppController extends Controller {

    protected static final String USER = "user";
    protected static final String VIEW = "view";
    protected static final String PAYLOAD = "payload";
    protected static final String PROFILE = "profile";
    protected static final String COURSES = "courses";
    protected static final String HISTORY = "history";
    protected static final String ACK = "ack";

    protected static final String ID = "id";
    protected static final String TAG = "tag";
    protected static final String NAME = "name";
    protected static final String ABBREV = "abbrev";
    protected static final String CORE = "core";
    protected static final String COURSE_ORDER_CSV = "courseOrderCsv";

    protected static final String COMMA = ",";
    protected static final String EMPTY = "";

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Creates a freshly minted object node.
     *
     * @return an empty object node
     */
    protected static ObjectNode objectNode() {
        return MAPPER.createObjectNode();
    }

    /**
     * Creates a freshly minted array node.
     *
     * @return an empty array node
     */
    protected static ArrayNode arrayNode() {
        return MAPPER.createArrayNode();
    }

    /**
     * Returns the value from the request body for the given key.
     * It is assumed that the body is formatted JSON, the key/value pair is
     * at the root level, and the value is a string.
     *
     * @param key the key
     * @return the value for the given key
     */
    protected static String fromRequest(String key) {
        JsonNode json = request().body().asJson();
        return json.findPath(key).textValue();
    }

    /**
     * Returns the array from the request body for the given key.
     * It is assumed that the body is formatted JSON, the key/value pair is
     * at the root level, and the value is an array.
     *
     * @param key the key
     * @return the array for the given key
     */
    protected static ArrayNode arrayFromRequest(String key) {
        JsonNode json = request().body().asJson();
        return (ArrayNode) json.withArray(key);
    }

    /**
     * Creates a response to send back to the UI (no payload).
     *
     * @param user the logged-in username
     * @param view the view name
     * @return the aggregated response data
     */
    protected static ObjectNode createResponse(String user, String view) {
        return createResponse(user, view, null);
    }

    /**
     * Creates a response to send back to the UI.
     *
     * @param user the logged-in username
     * @param view the view name
     * @param payload the payload data
     * @return the aggregated response data
     */
    protected static ObjectNode createResponse(String user, String view,
                                               ObjectNode payload) {
        ObjectNode response = objectNode()
                .put(USER, user)
                .put(VIEW, view);
        if (payload != null) {
            response.set(PAYLOAD, payload);
        }
        return response;
    }


    //------------------------------------------------------------------
    // TODO: delete these two methods, if we find we are not using them

    /**
     * Produces a CSV string from a list of integers.
     *
     * @param ints list of integers
     * @return CSV string
     */
    protected static String toCsv(List<Integer> ints) {
        StringBuilder sb = new StringBuilder();
        for (int i : ints) {
            sb.append(i).append(COMMA);
        }
        final int len = sb.length();
        sb.replace(len-1, len, EMPTY);
        return sb.toString();
    }

    /**
     * Produces a list of integers from a CSV string.
     *
     * @param csv the string
     * @return a list of integers
     */
    protected static List<Integer> fromCsv(String csv) {
        List<Integer> ints = new ArrayList<>();

        for (String i : csv.split(COMMA)) {
            ints.add(Integer.valueOf(i));
        }
        return ints;
    }

}
