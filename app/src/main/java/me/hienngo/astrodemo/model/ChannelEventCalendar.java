package me.hienngo.astrodemo.model;

import me.hienngo.astrodemo.ui.util.DateUtils;

/**
 * This object used to show the event on calendar
 * @author hienngo
 * @since 9/24/17
 */

public class ChannelEventCalendar {
    public String name;
    public String startTime;
    public long durationInMinutes;

    public ChannelEventCalendar(String name, String startTime, long durationInMinutes) {
        this.name = name;
        if (startTime != null && !startTime.equals("")) {
            this.startTime = DateUtils.getShortTimeStamp(DateUtils.parseUTCDate(startTime));
        }
        this.durationInMinutes = durationInMinutes;
    }
}
