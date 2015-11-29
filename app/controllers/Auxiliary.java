package controllers;

import models.Course;
import models.Student;
import models.StudentSolution;
import models.Transcript;
import play.Logger;
import play.mvc.Result;
import views.html.course;
import views.html.courses;
import views.html.student;
import views.html.studentResult;
import views.html.transcript;

import java.util.List;

/**
 * An auxiliary controller as somewhere to place prototype code, not part
 * of the main application.
 */
public class Auxiliary extends AppController {

    /**
     * Generates the courses page, listing all the available courses.
     *
     * @return courses page response
     */
    public static Result courses() {
        Logger.debug("courses page accessed");
        return ok(courses.render(Course.findAll()));
    }

    /**
     * Generates a page with information about the given student.
     *
     * @param id student ID
     * @return student information page
     */
    public static Result getStudent(String id) {
        Logger.debug("student page accessed, for student-id '{}'", id);
        return ok(student.render(Student.findById(id)));
    }

    /**
     * Generates a page with information about the given student's results
     *
     * @param username student user name
     * @return student information page
     */
    public static Result getStudentResult(String username) {
        Logger.debug("student result page accessed, for username '{}'", username);
        Student s = Student.findByUserName(username);
        List<StudentSolution> solns = StudentSolution.findByStudent(s);
        return ok(studentResult.render(s, solns));
    }

    /**
     * Generates a page with information about the given course.
     *
     * @param id course ID
     * @return course information page
     */
    public static Result getCourse(String id) {
        Logger.debug("course page accessed, for course-id '{}'", id);
        return ok(course.render(Course.findById(id)));
    }

    /**
     * Generates a page with information about the given transcript.
     *
     * @param id transcript ID
     * @return transcript information page
     */
    public static Result getTranscript(String id) {
        Logger.debug("student page accessed, for transcript-id '{}'", id);
        return ok(transcript.render(Transcript.findById(id)));
    }
}
