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
import me.hienngo.astrodemo.model.ChannelEventCalendar;
import me.hienngo.astrodemo.ui.util.DateUtils;
import me.hienngo.astrodemo.ui.util.GeneralUtils;

/**
 * @author hienngo
 * @since 9/22/17
 */

public class ChannelEventView extends RecyclerView {
    private List<ChannelEventCalendar> dataList;
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

    public List<ChannelEventCalendar> getDataList() {
        return dataList;
    }

    public void setDataList(List<ChannelEventCalendar> dataList) {
        this.dataList = dataList;
        getAdapter().notifyDataSetChanged();
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
            ChannelEventCalendar data = dataList.get(position);
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
