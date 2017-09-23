package me.hienngo.astrodemo.ui;

import java.util.concurrent.TimeUnit;

/**
 * @author hienngo
 * @since 9/23/17
 */

public interface Config {
    long EVENTS_PAGE_DURATION_IN_MINS = TimeUnit.HOURS.toMinutes(24);
    long EVENTS_PAGE_DURATION_IN_MILS = TimeUnit.HOURS.toMillis(24);
}
