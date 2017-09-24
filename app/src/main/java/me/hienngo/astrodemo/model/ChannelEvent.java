package me.hienngo.astrodemo.model;

import java.util.Calendar;
import java.util.List;

import me.hienngo.astrodemo.ui.util.DateUtils;

/**
 * @author hienngo
 * @since 9/22/17
 */

public class ChannelEvent extends Channel{
    public String eventID;
    public String programmeTitle;
    public String programmeId;
    public String displayDateTimeUtc;
    public String displayDateTime;
    public String displayDuration;
    private long endTimeInUtc;

    /**
     * @return time in mils
     */
    public long getStartTime() {
        return DateUtils.parseUTCDate(displayDateTimeUtc);
    }

    public int getDurationInMinutes() {
        try {
            String[] strings = displayDuration.split(":");
            return Integer.parseInt(strings[0]) * 60 + Integer.parseInt(strings[1]);
        } catch (Exception e) {
            return 0;
        }
    }

    public long getEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getStartTime());
        calendar.add(Calendar.MINUTE, getDurationInMinutes());
        return calendar.getTimeInMillis();
    }

    public static class Response extends RawResponse{
        public List<ChannelEvent> getevent;
    }
}
