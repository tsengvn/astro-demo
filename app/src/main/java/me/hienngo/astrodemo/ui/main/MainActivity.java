package me.hienngo.astrodemo.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.hienngo.astrodemo.AstroApp;
import me.hienngo.astrodemo.R;
import me.hienngo.astrodemo.domain.interactor.BookmarkManager;
import me.hienngo.astrodemo.domain.interactor.ChannelManager;
import me.hienngo.astrodemo.model.ChannelDetail;
import me.hienngo.astrodemo.model.ChannelEvent;
import me.hienngo.astrodemo.ui.Config;
import me.hienngo.astrodemo.ui.list.ChannelListActivity;
import me.hienngo.astrodemo.ui.util.GeneralUtils;

/**
 * @author hienngo
 * @since 9/22/17
 */

public class MainActivity extends MvpActivity<MainView, MainPresenter> implements MainView {
    @Inject
    ChannelManager channelManager;

    @Inject
    BookmarkManager bookmarkManager;

    @BindView(R.id.recyclerView1)
    RecyclerView recyclerView1;

    @BindView(R.id.recyclerView2)
    RecyclerView recyclerView2;

    private LeftChannelAdapter leftChannelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((AstroApp)getApplicationContext()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        presenter.loadData();
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter(channelManager, bookmarkManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
            startActivity(new Intent(this, ChannelListActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onReceivedChannelData(List<ChannelDetail> channelDetailList) {
        if (leftChannelAdapter == null) {
            leftChannelAdapter = new LeftChannelAdapter(this, channelDetailList);
            recyclerView1.setLayoutManager(new LinearLayoutManager(this));
            recyclerView1.setAdapter(leftChannelAdapter);
            recyclerView1.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));

        } else {
            leftChannelAdapter.setData(channelDetailList);
        }
        getPresenter().loadEvent(channelDetailList);
    }

    @Override
    public void onReceivedEventsData(Map<Long, List<ChannelEvent>> dataMap, long originTime) {
        if (recyclerView2.getAdapter() != null) {
            RightChannelAdapter oldAdapter = (RightChannelAdapter) recyclerView2.getAdapter();
            oldAdapter.clear();
        } else {
            GeneralUtils.syncVerticalScroll(recyclerView1, recyclerView2);
        }
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));
        recyclerView2.setAdapter(new RightChannelAdapter(this, leftChannelAdapter.channelDetails, dataMap, originTime));

    }

    static class LeftChannelAdapter extends RecyclerView.Adapter<LeftChannelAdapter.ViewHolder> {
        private final Context context;
        private final List<ChannelDetail> channelDetails;

        public LeftChannelAdapter(Context context, List<ChannelDetail> channelDetails) {
            this.context = context;
            this.channelDetails = new ArrayList<>();
            this.channelDetails.addAll(channelDetails);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_channel_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
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

    static class RightChannelAdapter extends RecyclerView.Adapter<RightChannelAdapter.ViewHolder>{
        private final Context context;
        private final List<ChannelDetail> channelDetails;
        private final Map<Long, List<ChannelEvent>> eventMap;
        private long originTime;
        private List<ChannelEventView> viewSyncScrollList = new ArrayList<>();

        public RightChannelAdapter(Context context, List<ChannelDetail> channelDetails, Map<Long, List<ChannelEvent>> eventMap, long originTime) {
            this.context = context;
            this.channelDetails = channelDetails;
            this.eventMap = eventMap;
            this.originTime = originTime;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ChannelEventView channelEventView = new ChannelEventView(context);
            return new ViewHolder(channelEventView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ChannelDetail channelDetail = channelDetails.get(position);
            ChannelEventView eventView = (ChannelEventView) holder.itemView;
            eventView.setDataList(originTime, Config.EVENTS_PAGE_DURATION_IN_MINS, eventMap.get(channelDetail.channelId));
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            ChannelEventView eventView = (ChannelEventView) holder.itemView;
            viewSyncScrollList.remove(eventView);
            eventView.removeOnScrollListener(scrollListener);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ChannelEventView eventView = (ChannelEventView) holder.itemView;
            viewSyncScrollList.add(eventView);
            eventView.addOnScrollListener(scrollListener);
        }

        @Override
        public int getItemCount() {
            return channelDetails.size();
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
                    .forEach(other -> other.removeOnScrollListener(scrollListener));
            viewSyncScrollList.clear();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
