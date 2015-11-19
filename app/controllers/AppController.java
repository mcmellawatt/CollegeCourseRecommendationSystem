package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.mvc.Controller;

/**
 * Base class for our controllers.
 */
public class AppController extends Controller {

    protected static final String USER = "user";
    protected static final String VIEW = "view";

    protected static final ObjectMapper MAPPER = new ObjectMapper();
    protected static ObjectNode objectNode() {
        return MAPPER.createObjectNode();
    }

    protected static ArrayNode arrayNode() {
        return MAPPER.createArrayNode();
    }

}
