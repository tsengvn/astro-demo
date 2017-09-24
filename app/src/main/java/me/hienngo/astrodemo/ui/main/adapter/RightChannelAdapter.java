package me.hienngo.astrodemo.ui.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import me.hienngo.astrodemo.model.ChannelDetail;
import me.hienngo.astrodemo.model.ChannelEventCalendar;
import me.hienngo.astrodemo.ui.ChannelSort;
import me.hienngo.astrodemo.ui.Config;
import me.hienngo.astrodemo.ui.main.ChannelEventView;
import me.hienngo.astrodemo.ui.main.MainPresenter;
import timber.log.Timber;

/**
 * @author hienngo
 * @since 9/23/17
 */

public class RightChannelAdapter extends RecyclerView.Adapter<RightChannelAdapter.ViewHolder> implements ChannelEventView.OnLoadMoreListener {
    private final Context context;
    private final MainPresenter mainPresenter;
    private List<ChannelDetail> channelDetails;
    private long originTime;
    private Map<Long, List<ChannelEventCalendar>> eventMap;
    private List<ChannelEventView> viewSyncScrollList = new ArrayList<>();
    private boolean isLoadingMore = false;

    public RightChannelAdapter(Context context,
                               MainPresenter mainPresenter, List<ChannelDetail> channelDetails,
                               Map<Long, List<ChannelEventCalendar>> eventMap, long originTime) {
        this.context = context;
        this.mainPresenter = mainPresenter;
        this.channelDetails = new ArrayList<>();
        this.eventMap = eventMap;
        this.originTime = originTime;
        this.channelDetails.addAll(channelDetails);
    }

    @Override
    public RightChannelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChannelEventView channelEventView = new ChannelEventView(context);
        return new RightChannelAdapter.ViewHolder(channelEventView);
    }

    @Override
    public void onBindViewHolder(RightChannelAdapter.ViewHolder holder, int position) {
        ChannelDetail channelDetail = channelDetails.get(position);
        ChannelEventView eventView = (ChannelEventView) holder.itemView;
        eventView.setDataList(eventMap.get(channelDetail.channelId));
        eventView.setLoadMoreListener(this);
        Timber.i("bind view %s data size %s", position, eventMap.get(channelDetail.channelId).size());
    }

    @Override
    public void onViewDetachedFromWindow(RightChannelAdapter.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ChannelEventView eventView = (ChannelEventView) holder.itemView;
        viewSyncScrollList.remove(eventView);
        eventView.removeOnScrollListener(scrollListener);
    }

    @Override
    public void onViewAttachedToWindow(RightChannelAdapter.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ChannelEventView eventView = (ChannelEventView) holder.itemView;
        viewSyncScrollList.add(eventView);
        eventView.addOnScrollListener(scrollListener);
    }

    @Override
    public int getItemCount() {
        return channelDetails.size();
    }

    public void loadMoreEvent() {
        isLoadingMore = true;
        mainPresenter.loadEvent(channelDetails,
                originTime + Config.EVENTS_PAGE_DURATION_IN_MILS,
                true);

    }

    public void onReceiveNextEvents(Map<Long, List<ChannelEventCalendar>> updateEventMap) {
        for (Long key : eventMap.keySet()) {
            List<ChannelEventCalendar> eventCalendars = eventMap.get(key);
            eventCalendars.addAll(updateEventMap.get(key));
            eventMap.put(key, eventCalendars);
        }
        isLoadingMore = false;
        originTime += Config.EVENTS_PAGE_DURATION_IN_MILS;
        notifyDataSetChanged();
    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            Stream.of(viewSyncScrollList)
                    .filter(other -> other != recyclerView)
                    .forEach(other -> {
                        other.removeOnScrollListener(this);
                        other.scrollBy(dx, 0);
                        other.addOnScrollListener(this);
                    });
        }

//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    Stream.of(viewSyncScrollList)
//                            .filter(other -> other != recyclerView)
//                            .forEach(other -> {
//                                other.removeOnScrollListener(this);
//                                other.setScrollX(recyclerView.getScrollY());
//                                other.addOnScrollListener(this);
//                            });
//                }
//            }
    };

    public void clear() {
        Stream.of(viewSyncScrollList)
                .forEach(other -> {
                    other.removeOnScrollListener(scrollListener);
                    other.clear();
                });
        viewSyncScrollList.clear();
        channelDetails.clear();
        notifyDataSetChanged();
    }

    public void sort(ChannelSort name) {
        switch (name) {
            case name:
                Collections.sort(channelDetails, (t1, t2) -> t1.channelTitle.compareTo(t2.channelTitle));
                break;
            case number:
                Collections.sort(channelDetails, (t1, t2) -> Long.valueOf(t1.channelStbNumber)
                        .compareTo(t2.channelStbNumber));
                break;
        }
        notifyDataSetChanged();
    }

    @Override
    public void onLoadMore() {
        if (!isLoadingMore) {
            loadMoreEvent();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
