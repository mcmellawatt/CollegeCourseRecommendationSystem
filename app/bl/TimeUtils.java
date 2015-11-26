package bl;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Encapsulate what we wish to do with timestamps.
 */
public class TimeUtils {

    private static final String DATE_FMT = "dd-MMM-yyyy HH:mm:ss";
    private static final SimpleDateFormat SDF = new SimpleDateFormat(DATE_FMT);

    /**
     * Returns the epoch timestamp for right now.
     *
     * @return timestamp for now
     */
    public static long now() {
        return System.currentTimeMillis();
    }

    /**
     * Convert the given timestamp to displayable string.
     *
     * @param ts timestamp (epoch ms)
     * @return displayable string of the time
     */
    public static String toDisplayString(long ts) {
        return SDF.format(new Date(ts));
    }
}
