package me.hienngo.astrodemo.ui.main;

import android.content.Context;
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
import me.hienngo.astrodemo.ui.util.GeneralUtils;

/**
 * @author hienngo
 * @since 9/22/17
 */

public class ChannelEventView extends RecyclerView {
    private List<ChannelEventCalendar> dataList;
    private OnLoadMoreListener loadMoreListener;
    private Adapter adapter;
    public ChannelEventView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        dataList = new ArrayList<>();
        int height = context.getResources().getDimensionPixelSize(R.dimen.channel_item_height);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height));
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            protected int getExtraLayoutSpace(State state) {
                return 0;
            }
        });
//        addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));
        adapter = new Adapter();
        setAdapter(adapter);
    }

    public void setDataList(List<ChannelEventCalendar> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        adapter.notifyDataSetChanged();
    }

    public void clear() {
        dataList.clear();
        adapter.notifyDataSetChanged();
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
//        int[] colors = new int[]{Color.RED, Color.BLUE, Color.CYAN, Color.YELLOW};

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
            holder.itemView.setBackgroundColor(data.backgroundColor);

            if (position == dataList.size()-1 && loadMoreListener != null) {
                loadMoreListener.onLoadMore();
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView1, textView2;
        ViewHolder(View itemView) {
            super(itemView);
            textView1 = ButterKnife.findById(itemView, R.id.tvEventName);
            textView2 = ButterKnife.findById(itemView, R.id.tvEventTime);
        }
    }


}
