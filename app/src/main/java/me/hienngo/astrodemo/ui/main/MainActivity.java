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

import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.hienngo.astrodemo.AstroApp;
import me.hienngo.astrodemo.R;
import me.hienngo.astrodemo.domain.interactor.BookmarkManager;
import me.hienngo.astrodemo.domain.interactor.ChannelManager;
import me.hienngo.astrodemo.model.ChannelDetail;
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
    public void onReceivedData(List<ChannelDetail> channelDetailList) {
        if (leftChannelAdapter == null) {
            leftChannelAdapter = new LeftChannelAdapter(this, channelDetailList);
            recyclerView1.setLayoutManager(new LinearLayoutManager(this));
            recyclerView1.setAdapter(leftChannelAdapter);
            recyclerView1.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));

            GeneralUtils.syncVerticalScroll(recyclerView1, recyclerView2);
        } else {
            leftChannelAdapter.setData(channelDetailList);
        }
    }

    public static class LeftChannelAdapter extends RecyclerView.Adapter<LeftChannelAdapter.ViewHolder> {
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
}