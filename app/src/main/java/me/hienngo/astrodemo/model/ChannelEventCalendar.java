package me.hienngo.astrodemo.model;

import android.graphics.Color;

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
    public int backgroundColor;

    public ChannelEventCalendar(String name, String startTime, long durationInMinutes, int backgroundColor) {
        this.name = name;
        if (startTime != null && !startTime.equals("")) {
            this.startTime = DateUtils.getShortTimeStamp(DateUtils.parseDate(startTime));
        }
//        this.startTime = startTime;
        this.durationInMinutes = durationInMinutes;
        this.backgroundColor = backgroundColor;
    }
    public ChannelEventCalendar(String name, String startTime, long durationInMinutes) {
        this(name, startTime, durationInMinutes, Color.WHITE);
    }

    public static ChannelEventCalendar empty(long durationInMinutes) {
        return new ChannelEventCalendar("", "", durationInMinutes, Color.GRAY);
    }
}
