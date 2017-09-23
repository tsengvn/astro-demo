package me.hienngo.astrodemo.ui.main;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import me.hienngo.astrodemo.R;
import me.hienngo.astrodemo.model.ChannelEvent;
import me.hienngo.astrodemo.ui.util.DateUtils;
import me.hienngo.astrodemo.ui.util.GeneralUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author hienngo
 * @since 9/22/17
 */

public class ChannelEventView extends RecyclerView {
    private List<Data> dataList;
    private long originTime;
    public ChannelEventView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        dataList = new ArrayList<>();
        int height = context.getResources().getDimensionPixelSize(R.dimen.channel_item_height);
        setOverScrollMode(OVER_SCROLL_NEVER);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height));
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));
        setAdapter(new Adapter());
    }

    public void setDataList(long originTimeInMils, long totalDurationInMins, List<ChannelEvent> channelEvents) {
        this.dataList.clear();
        getAdapter().notifyDataSetChanged();
        Observable.from(channelEvents)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toSortedList((t1, t2) -> Long.valueOf(t1.getStartTimeInUtc()).compareTo(t2.getStartTimeInUtc()))
                .map(sorted -> mapEventToData(sorted, originTimeInMils, totalDurationInMins))
                .subscribe(dataList -> {
                    this.dataList.addAll(dataList);
                    getAdapter().notifyDataSetChanged();
                });
    }

    private List<Data> mapEventToData(List<ChannelEvent> sorted, long originTimeInMils, long totalDurationInMins) {
        List<Data> dataList = new ArrayList<>();
        this.originTime = originTimeInMils;
        long endTime = originTimeInMils + totalDurationInMins * 60 * 1000;
        for (ChannelEvent event : sorted) {
            if (event.getStartTimeInUtc() < originTime && event.getEndTimeInUtc() > originTime) {
                //event already start before origin time
                //reduce event duration
                long durationInMin = (event.getEndTimeInUtc() - originTime) / (1000*60);
                dataList.add(new Data(event.programmeTitle, event.displayDateTime, durationInMin));
                originTime = event.getEndTimeInUtc();
            } else if (event.getStartTimeInUtc() == originTime && event.getEndTimeInUtc() < endTime) {
                dataList.add(new Data(event.programmeTitle, event.displayDateTime, event.getDurationInMinutes()));
                originTime = event.getEndTimeInUtc();
            } else if (event.getStartTimeInUtc() > originTime && event.getEndTimeInUtc() < endTime) {
                //create space
                long spaceDurationInMin = (event.getStartTimeInUtc() - originTime)/ (1000*60);
                dataList.add(new Data("Empty", "", spaceDurationInMin));

                dataList.add(new Data(event.programmeTitle, event.displayDateTime, event.getDurationInMinutes()));
                originTime = event.getEndTimeInUtc();
            } else if (event.getEndTimeInUtc() > endTime) {
                //reduce event duration
                long newDuration = (endTime - event.getStartTimeInUtc()) / (1000*60);
                dataList.add(new Data(event.programmeTitle, event.displayDateTime, newDuration));
                originTime = endTime;
            }
        }

        if (originTime != endTime) {
            //create space
            long spaceDurationInMin = (endTime - originTime)/ (1000*60);
            dataList.add(new Data("Empty", "", spaceDurationInMin));
            originTime = endTime;
        }
        return dataList;
    }

    public void clear() {
        dataList.clear();
        getAdapter().notifyDataSetChanged();
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.adapter_channel_event, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Data data = dataList.get(position);
            holder.textView1.setText(data.name);
            holder.textView2.setText(data.startTime);
            int height = getResources().getDimensionPixelSize(R.dimen.channel_item_height);
            holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(
                    GeneralUtils.convertMinutesToWidth(getContext(), data.durationInMinutes),
                    height)
            );

        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView1, textView2;
        ViewHolder(View itemView) {
            super(itemView);
            textView1 = ButterKnife.findById(itemView, R.id.tvEventName);
            textView2 = ButterKnife.findById(itemView, R.id.tvEventTime);
        }
    }


    private static class Data {
        String name;
        String startTime;
        long durationInMinutes;

        private Data(String name, String startTime, long durationInMinutes) {
            this.name = name;
            if (startTime != null && !startTime.equals("")) {
                this.startTime = DateUtils.getShortTimeStamp(DateUtils.parseUTCDate(startTime));
            }
            this.durationInMinutes = durationInMinutes;
        }
    }


}
