package bl;

/**
 * Provides user login authentication.
 */
public interface Authenticator {

    /**
     * Authenticates the given user name against the given password.
     *
     * @param user username
     * @param pass password
     * @return true if the combination is authenticated; false otherwise
     */
    boolean authenticate(String user, String pass);
}
