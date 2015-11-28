package bl;

import models.Student;

/**
 * An authentication implementation that uses username/password data
 * embedded in the student records.
 */
public final class StudentBeanAuthenticator implements Authenticator {

    @Override
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
