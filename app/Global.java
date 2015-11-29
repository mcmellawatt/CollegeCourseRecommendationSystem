import bl.SchedulerService;
import com.avaje.ebean.Ebean;
import models.Student;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Yaml;

import java.util.List;
import java.util.Map;

/**
 * Start up and shutdown of our application.
 */
public final class Global extends GlobalSettings {

    private static final String INITIAL_DATA_FILE = "initial-data.yml";
    private static final String COURSES = "courses";
    private static final String STUDENTS = "students";
    private static final String TRANSCRIPTS = "transcripts";

    @Override
    public void onStart(Application app) {
        Logger.info("Starting application");
        loadInitialData();
        SchedulerService.SINGLETON.start();
        Logger.info("Started");
    }

    @Override
    public void onStop(Application app) {
        Logger.info("Stopping application");
        SchedulerService.SINGLETON.stop();
        Logger.info("Stopped");
    }

    @SuppressWarnings("unchecked")
    private void loadInitialData() {
        if (Student.findAll().isEmpty()) {
            Map<String, List<?>> all =
                    (Map<String, List<?>>) Yaml.load(INITIAL_DATA_FILE);

            Ebean.save(all.get(COURSES));
            Ebean.save(all.get(TRANSCRIPTS));
            Ebean.save(all.get(STUDENTS));
        }
    }
}