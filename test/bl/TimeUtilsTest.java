package bl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link TimeUtils}.
 */
public class TimeUtilsTest {

    private static final long XMAS = 1419494400000L;

    @Test
    public void xmas() {
        String display = TimeUtils.toDisplayString(XMAS);
        assertEquals("wrong time", "25-Dec-2014 00:00:00", display);
    }

}
