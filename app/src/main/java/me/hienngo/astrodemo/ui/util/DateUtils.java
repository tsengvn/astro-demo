package me.hienngo.astrodemo.ui.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author hienngo
 * @since 9/22/17
 */

public class DateUtils {
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final SimpleDateFormat TIME_FORMAT_FULL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("YYYY-MM-dd HH:mm");
    private static final SimpleDateFormat SHORT_TIME_FORMAT = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

    static {
        TIME_FORMAT.setTimeZone(UTC);
    }

    public static long parseUTCDate(String value) {
        try {
            return TIME_FORMAT_FULL.parse(value).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getTimeStampUTC(long timeInMils) {
        return TIME_FORMAT.format(new Date(timeInMils));
    }

    public static String getTimeStampUTC(Date date) {
        return TIME_FORMAT.format(date);
    }

    /**
     *
     * @param timeInMils
     * @return timestamp 'MM-dd HH:mm' based on device timezone
     */
    public static String getShortTimeStamp(long timeInMils) {
        return SHORT_TIME_FORMAT.format(timeInMils);
    }
}
