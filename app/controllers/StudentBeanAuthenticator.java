package controllers;

import models.Student;

/**
 * An authentication implementation that uses username/password data from the
 * student records.
 */
public final class StudentBeanAuthenticator {

    /**
     * Authenticates the given user name against the given password.
     *
     * @param user username
     * @param pass password
     * @return true if the combination is authenticated; false otherwise
     */
    public boolean authenticate(String user, String pass) {
        if (user != null) {
            Student student = Student.findByUserName(user);
            if (student != null) {
                String expPass = student.password;
                return (expPass != null && expPass.equals(pass));
            }
        }
        return false;
    }
}
