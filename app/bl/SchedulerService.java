package bl;

import play.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerService {

    private static final long PERIOD = 5;

    ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

    public SchedulerService() {
    }

    public void start() {
        exec.scheduleAtFixedRate(new SolveTask(), PERIOD, PERIOD, TimeUnit.SECONDS);
    }

    public void stop() {
        exec.shutdown();
    }

    private static class SolveTask implements Runnable {

        @Override
        public void run() {
            // do stuff
            Logger.debug("scheduled task run.");
        }
    }
}