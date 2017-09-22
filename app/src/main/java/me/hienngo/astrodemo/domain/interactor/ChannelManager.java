package me.hienngo.astrodemo.domain.interactor;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.hienngo.astrodemo.domain.repo.AstroRepo;
import me.hienngo.astrodemo.model.Channel;
import me.hienngo.astrodemo.model.ChannelDetail;
import me.hienngo.astrodemo.model.ChannelEvent;
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTimeInMils);
        calendar.add(Calendar.HOUR, 24);
        final String periodStart = DateUtils.getTimeStamp(startTimeInMils);
        final String periodEnd = DateUtils.getTimeStamp(calendar.getTimeInMillis());
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

}
