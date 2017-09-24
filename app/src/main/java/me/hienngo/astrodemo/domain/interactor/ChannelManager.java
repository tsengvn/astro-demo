package me.hienngo.astrodemo.domain.interactor;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.hienngo.astrodemo.domain.repo.AstroRepo;
import me.hienngo.astrodemo.model.Channel;
import me.hienngo.astrodemo.model.ChannelDetail;
import me.hienngo.astrodemo.model.ChannelEvent;
import me.hienngo.astrodemo.model.ChannelEventCalendar;
import me.hienngo.astrodemo.ui.Config;
import me.hienngo.astrodemo.ui.util.DateUtils;
import rx.Observable;

/**
 * @author hienngo
 * @since 9/21/17
 */

public class ChannelManager {
    private final AstroRepo astroRepo;

    public ChannelManager(AstroRepo astroRepo) {
        this.astroRepo = astroRepo;
    }

    public Observable<List<Channel>> getChannelList() {
        return astroRepo.getChannelList().map(response -> response.channels);
    }

    public Observable<List<ChannelDetail>> getChannelsDetail(List<Long> ids) {
        String idsArray = Stream.of(ids).map(String::valueOf).reduce((id1, id2) -> id1 + "," + id2).orElse("");
        return astroRepo.getChannelsWithMetadata(idsArray).map(response -> response.channel);
    }

    public Observable<Map<Long, List<ChannelEvent>>> getChannelEventIn24h(List<Long> ids, long startTimeInMils) {
        final String periodStart = DateUtils.getTimeStampInGMT8(startTimeInMils);
        final String periodEnd = DateUtils.getTimeStampInGMT8(startTimeInMils + Config.EVENTS_PAGE_DURATION_IN_MILS);
        String idsArray = Stream.of(ids).map(String::valueOf).reduce((id1, id2) -> id1 + "," + id2).orElse("");

        return astroRepo.getEvents(idsArray, periodStart, periodEnd)
                .map(response -> {
                    HashMap<Long, List<ChannelEvent>> map = new HashMap<>();
                    for (ChannelEvent channelEvent : response.getevent) {
                        List<ChannelEvent> eventList;
                        if (map.containsKey(channelEvent.channelId)) {
                            eventList = map.get(channelEvent.channelId);
                        } else {
                            eventList = new ArrayList<>();
                        }
                        eventList.add(channelEvent);
                        map.put(channelEvent.channelId, eventList);
                    }
                    return map;
                });
    }

    public Observable<Map<Long, List<ChannelEventCalendar>>> getEventCalendarIn24h(List<Long> ids, long startTimeInMils) {
        return getChannelEventIn24h(ids, startTimeInMils)
                .map(rawEventMap -> {
                    Map<Long, List<ChannelEventCalendar>> eventsCalendarMap = new HashMap<>();
                    for (Long eventId : rawEventMap.keySet()) {
                        eventsCalendarMap.put(eventId, mapEventsToCalendar(rawEventMap.get(eventId), startTimeInMils));
                    }
                    return eventsCalendarMap;
                });
    }


    private List<ChannelEventCalendar> mapEventsToCalendar(List<ChannelEvent> channelEvents, long originTimeInMils) {
        Collections.sort(channelEvents, (c1, c2) -> Long.valueOf(c1.getStartTime()).compareTo(c2.getStartTime()));

        List<ChannelEventCalendar> dataList = new ArrayList<>();
        long originTime = originTimeInMils;
        long endTime = originTimeInMils + Config.EVENTS_PAGE_DURATION_IN_MILS;
        for (ChannelEvent event : channelEvents) {
            if (event.getStartTime() < originTime && event.getEndTime() > originTime) {
                //event already start before origin time
                //reduce event duration
                long durationInMin = (event.getEndTime() - originTime) / (1000*60);
                dataList.add(new ChannelEventCalendar("On-going: "+ event.programmeTitle, event.displayDateTime, durationInMin));
                originTime = event.getEndTime();
            } else if (event.getEndTime() > endTime) {
                //reduce event duration
                long newDuration = (endTime - event.getStartTime()) / (1000*60);
                dataList.add(new ChannelEventCalendar(event.programmeTitle, event.displayDateTime, newDuration));
                originTime = endTime;
            } else {
                //create space
                if (event.getStartTime() > originTime && event.getEndTime() < endTime) {
                    long spaceDurationInMin = (event.getStartTime() - originTime)/ (1000*60);
                    dataList.add(ChannelEventCalendar.empty(spaceDurationInMin));
                }

                dataList.add(new ChannelEventCalendar(event.programmeTitle, event.displayDateTime, event.getDurationInMinutes()));
                originTime = event.getEndTime();
            }
        }

        if (originTime != endTime) {
            //create space
            long spaceDurationInMin = (endTime - originTime)/ (1000*60);
            dataList.add(ChannelEventCalendar.empty(spaceDurationInMin));
            originTime = endTime;
        }
        return dataList;
    }
}
