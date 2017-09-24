package me.hienngo.astrodemo.ui.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import me.hienngo.astrodemo.R;
import me.hienngo.astrodemo.model.ChannelDetail;
import me.hienngo.astrodemo.ui.ChannelSort;

/**
 * @author hienngo
 * @since 9/23/17
 */

public class LeftChannelAdapter extends RecyclerView.Adapter<LeftChannelAdapter.ViewHolder> {
    private final Context context;
    private final List<ChannelDetail> channelDetails;

    public LeftChannelAdapter(Context context, List<ChannelDetail> channelDetails) {
        this.context = context;
        this.channelDetails = new ArrayList<>();
        this.channelDetails.addAll(channelDetails);
    }

    @Override
    public LeftChannelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LeftChannelAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_channel_item, parent, false));
    }

    @Override
    public void onBindViewHolder(LeftChannelAdapter.ViewHolder holder, int position) {
        final ChannelDetail channelDetail = channelDetails.get(position);
        holder.channelNameView.setText(channelDetail.channelTitle);
        holder.channelDescView.setText(channelDetail.channelCategory);
        Picasso.with(context).load(channelDetail.getLogoUrl()).fit().centerInside().into(holder.logoView);
    }

    @Override
    public int getItemCount() {
        return channelDetails.size();
    }

    public void setData(List<ChannelDetail> data) {
        this.channelDetails.clear();
        this.channelDetails.addAll(data);
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

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView channelNameView, channelDescView;
        public ImageView logoView;
        public ViewHolder(View itemView) {
            super(itemView);
            channelNameView = ButterKnife.findById(itemView, R.id.tvChannelName);
            channelDescView = ButterKnife.findById(itemView, R.id.tvDescription);
            logoView = ButterKnife.findById(itemView, R.id.ivLogo);
        }
    }
}