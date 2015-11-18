import com.avaje.ebean.Ebean;
import play.Application;
import play.GlobalSettings;
import play.libs.Yaml;

import java.util.List;
import java.util.Map;
import models.Student;

/**
 * Sets up our application's global settings.
 */
public class Global extends GlobalSettings {

    private static final String INITIAL_DATA_FILE = "initial-data.yml";
    private static final String COURSES = "courses";
    private static final String STUDENTS = "students";
    private static final String TRANSCRIPTS = "transcripts";

    @Override
    public void onStart(Application app) {
        InitialData.insert(app);
    }

    private static class InitialData {
        private static void insert(Application app) {
            if (Student.findAll().size() == 0) {
                Map<String, List<?>> all =
                        (Map<String, List<?>>) Yaml.load(INITIAL_DATA_FILE);

                Ebean.save(all.get(COURSES));
                Ebean.save(all.get(TRANSCRIPTS));
                Ebean.save(all.get(STUDENTS));
            }
        }
    }
}