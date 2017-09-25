package me.hienngo.astrodemo.model;

import android.graphics.Color;
import android.text.TextUtils;

import me.hienngo.astrodemo.ui.util.DateUtils;

/**
 * This object used to show the event on calendar
 * @author hienngo
 * @since 9/24/17
 */

public class ChannelEventCalendar {
    public String name;
    public long startTimeInMils;
    public long endTimeInMils;
    public String startTimeStamp;
    public long durationInMinutes;
    public int backgroundColor;

    public ChannelEventCalendar(String name, long startTime, long endTime, long durationInMinutes, int backgroundColor) {
        this.name = name;
        this.startTimeInMils = startTime;
        this.endTimeInMils = endTime;
        if (startTime != 0) {
            this.startTimeStamp = DateUtils.getShortTimeStamp(startTimeInMils);
        }

        this.durationInMinutes = durationInMinutes;
        this.backgroundColor = backgroundColor;
    }
    public ChannelEventCalendar(String name, long startTime, long endTime, long durationInMinutes) {
        this(name, startTime, endTime, durationInMinutes, Color.WHITE);
    }

    public static ChannelEventCalendar empty(long durationInMinutes) {
        return new ChannelEventCalendar("", 0, 0, durationInMinutes, Color.GRAY);
    }

    public static boolean isEmpty(ChannelEventCalendar eventCalendar) {
        return TextUtils.isEmpty(eventCalendar.name) && eventCalendar.backgroundColor == Color.GRAY;
    }
}
