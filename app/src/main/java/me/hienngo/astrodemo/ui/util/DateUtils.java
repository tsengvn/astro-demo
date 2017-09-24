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
    private static final SimpleDateFormat PARSE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat PARSE_TIME_FORMAT_UTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat TIME_STAMP_FORMAT_GMT8 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private static final SimpleDateFormat SHORT_TIME_STAMP_FORMAT = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

    static {
        PARSE_TIME_FORMAT_UTC.setTimeZone(UTC);
        TIME_STAMP_FORMAT_GMT8.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    }

    public static long parseUTCDate(String value) {
        try {
            return PARSE_TIME_FORMAT_UTC.parse(value).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long parseDate(String value) {
        try {
            return PARSE_TIME_FORMAT.parse(value).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public static String getTimeStampInGMT8(long timeInMils) {
        return TIME_STAMP_FORMAT_GMT8.format(new Date(timeInMils));
    }

    /**
     *
     * @param timeInMils
     * @return timestamp 'MM-dd HH:mm' based on device timezone
     */
    public static String getShortTimeStamp(long timeInMils) {
        return SHORT_TIME_STAMP_FORMAT.format(timeInMils);
    }
}
