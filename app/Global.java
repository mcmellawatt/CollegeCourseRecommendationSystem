import play.*;
import play.libs.*;

import java.util.*;

import com.avaje.ebean.*;

import models.*;

public class Global extends GlobalSettings {

    public void onStart(Application app) {
        InitialData.insert(app);
    }

    static class InitialData {

        public static void insert(Application app) {
                Map<String,List<?>> all = (Map<String,List<?>>)Yaml.load("initial-data.yml");

                Ebean.save(all.get("courses"));
                Ebean.save(all.get("students"));

            }
        }
}