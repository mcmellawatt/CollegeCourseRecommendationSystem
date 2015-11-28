package bl;

import models.Student;

/**
 * An authentication implementation that uses username/password data
 * embedded in the student records.
 */
public final class StudentBeanAuthenticator implements Authenticator {

    private static final String ADMIN = "admin";

    @Override
    public boolean authenticate(String user, String pass) {
        // special hook for admin user.
        // yeah - bad practice hard coding poor password, but
        //  this is for demonstration purposes only
        if (ADMIN.equals(user) && ADMIN.equals(pass)) {
            return true;
        }

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
