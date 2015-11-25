import com.avaje.ebean.Ebean;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Yaml;

import java.util.List;
import java.util.Map;
import models.Student;

/**
 * Start up and shutdown of our application.
 */
public final class Global extends GlobalSettings {

    private static final String INITIAL_DATA_FILE = "initial-data.yml";
    private static final String COURSES = "courses";
    private static final String STUDENTS = "students";
    private static final String TRANSCRIPTS = "transcripts";

    private final MyScheduler sched = new MyScheduler();


    @Override
    public void onStart(Application app) {
        Logger.info("Starting application");
        loadInitialData();
        sched.start();
        Logger.info("Started");
    }

    @Override
    public void onStop(Application app) {
        Logger.info("Stopping application");
        sched.shutdown();
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


    // -------------------------------------------------------------------
    // temp class till JP implements his... (delete this code afterwards)
    private static class MyScheduler {
        public void start() {
            // init executor and start up the periodic task
        }

        public void shutdown() {
            // shutdown the executor
        }
    }

}