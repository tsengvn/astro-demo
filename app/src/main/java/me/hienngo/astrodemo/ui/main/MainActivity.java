package me.hienngo.astrodemo.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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

import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
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
import me.hienngo.astrodemo.model.ChannelEventCalendar;
import me.hienngo.astrodemo.ui.ChannelSort;
import me.hienngo.astrodemo.ui.list.ChannelListActivity;
import me.hienngo.astrodemo.ui.main.adapter.RightChannelAdapter;
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
    private RightChannelAdapter rightChannelAdapter;
    private Snackbar snackbar;

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
        switch (item.getItemId()) {
            case R.id.menu_add:
                startActivity(new Intent(this, ChannelListActivity.class));
                return true;
            case R.id.menu_sort_name:
                if (leftChannelAdapter != null && rightChannelAdapter != null) {
                    leftChannelAdapter.sort(ChannelSort.name);
                    rightChannelAdapter.sort(ChannelSort.name);
                }
                return true;
            case R.id.menu_sort_number:
                if (leftChannelAdapter != null && rightChannelAdapter != null) {
                    leftChannelAdapter.sort(ChannelSort.number);
                    rightChannelAdapter.sort(ChannelSort.number);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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
    public void onReceivedCalendarData(Map<Long, List<ChannelEventCalendar>> dataMap, long originTime, boolean loadMore) {
        if (loadMore) {
            rightChannelAdapter.onReceiveNextEvents(dataMap);
        } else if (rightChannelAdapter != null) {
            rightChannelAdapter.clear();
            rightChannelAdapter = new RightChannelAdapter(this, getPresenter(), leftChannelAdapter.channelDetails, dataMap, originTime);
            recyclerView2.setAdapter(rightChannelAdapter);
        } else {
            recyclerView2.setLayoutManager(new LinearLayoutManager(this));
            recyclerView2.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));
            GeneralUtils.syncVerticalScroll(recyclerView1, recyclerView2);
            rightChannelAdapter = new RightChannelAdapter(this, getPresenter(), leftChannelAdapter.channelDetails, dataMap, originTime);
            recyclerView2.setAdapter(rightChannelAdapter);
        }


    }

    @Override
    public void showLoading() {
        snackbar = Snackbar.make(findViewById(R.id.root), "Loading", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    @Override
    public void hideLoading() {
        if (snackbar != null) {
            snackbar.dismiss();
            snackbar = null;
        }
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
            holder.channelNameView.setText(channelDetail.channelTitle + " " + channelDetail.channelStbNumber);
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

}
