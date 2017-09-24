package me.hienngo.astrodemo.ui.main;

import com.hannesdorfmann.mosby3.mvp.MvpView;

import java.util.List;
import java.util.Map;

import me.hienngo.astrodemo.model.ChannelDetail;
import me.hienngo.astrodemo.model.ChannelEventCalendar;

/**
 * @author hienngo
 * @since 9/22/17
 */

public interface MainView extends MvpView{
    void onReceivedChannelData(List<ChannelDetail> channelDetailList);

    void onReceivedCalendarData(Map<Long, List<ChannelEventCalendar>> dataMap, long originTime);
}
