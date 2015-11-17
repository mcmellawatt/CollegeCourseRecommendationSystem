package controllers;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates authentication behavior.
 */
public final class Authenticator {

    private static final Map<String, String> LOGIN_DB = new HashMap<>();
    static {
        LOGIN_DB.put("jesse", "stills");
        LOGIN_DB.put("jp", "reilland");
        LOGIN_DB.put("ryan", "mcmahon");
        LOGIN_DB.put("simon", "hunt");
    }

    /**
     * Authenticates the given user name against the given password.
     *
     * @param user username
     * @param pass password
     * @return true if the combination is authenticated; false otherwise
     */
    public boolean authenticate(String user, String pass) {
        if (user != null) {
            String exp_pass = LOGIN_DB.get(user);
            return (exp_pass != null && exp_pass.equals(pass));
        }
        return false;
    }
}
