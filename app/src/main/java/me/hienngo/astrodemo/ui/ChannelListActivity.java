package me.hienngo.astrodemo.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import me.hienngo.astrodemo.AstroApp;
import me.hienngo.astrodemo.R;
import me.hienngo.astrodemo.domain.interactor.BookmarkManager;
import me.hienngo.astrodemo.domain.interactor.ChannelManager;
import me.hienngo.astrodemo.model.Channel;

/**
 * @author hienngo
 * @since 9/21/17
 */

public class ChannelListActivity extends MvpActivity<ChannelListView, ChannelListPresenter> implements ChannelListView {

    @Inject
    ChannelManager channelManager;

    @Inject
    BookmarkManager bookmarkManager;

    private ChannelAdapter channelAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((AstroApp)getApplicationContext()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_list_channel);
        getPresenter().loadChannelData();

    }

    @NonNull
    @Override
    public ChannelListPresenter createPresenter() {
        return new ChannelListPresenter(channelManager, bookmarkManager);
    }

    @Override
    public void setData(List<Channel> channelList, List<Channel> bookmarkList) {
        RecyclerView recyclerView = ButterKnife.findById(this, R.id.recyclerView);
        if (channelAdapter == null) {
            channelAdapter = new ChannelAdapter(this, channelList, bookmarkList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(channelAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.channel_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_sort_name:
                if (channelAdapter != null) channelAdapter.setSortType(ChannelAdapter.SortType.name);
                return true;
            case R.id.menu_sort_number:
                if (channelAdapter != null) channelAdapter.setSortType(ChannelAdapter.SortType.number);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (channelAdapter != null) {
            bookmarkManager.sync(channelAdapter.getBookmarkedList());
        }

    }

    public static class ChannelAdapter extends RecyclerView.Adapter<ChannelViewHolder> {
        enum SortType {
            name, number;
        }
        private final Context context;
        private final List<Channel> dataList;
        private SortType sortType = SortType.name;
        private List<Channel> bookmarkedList;
        public ChannelAdapter(Context context, List<Channel> dataList, List<Channel> bookmarkedList) {
            this.context = context;
            this.dataList = dataList;
            this.bookmarkedList = bookmarkedList;
            sort();
        }

        @Override
        public ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ChannelViewHolder(LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_checked, parent, false));
        }

        @Override
        public void onBindViewHolder(final ChannelViewHolder holder, final int position) {
            final Channel channel = dataList.get(position);
            holder.textView.setText(new StringBuilder()
                    .append(channel.channelTitle)
                    .append("\n")
                    .append(channel.channelStbNumber));
            holder.textView.setChecked(bookmarkedList.contains(channel));
            holder.textView.setOnClickListener(view -> {
                if (bookmarkedList.contains(channel)) {
                    bookmarkedList.remove(channel);
                } else {
                    bookmarkedList.add(channel);
                }
                notifyItemChanged(position);
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public void setSortType(SortType sortType) {
            this.sortType = sortType;
            sort();
        }

        private void sort() {
            switch (sortType) {
                case name:
                    Collections.sort(dataList, (t1, t2) -> t1.channelTitle.compareTo(t2.channelTitle));
                    break;
                case number:
                    Collections.sort(dataList, (t1, t2) -> Long.valueOf(t1.channelStbNumber)
                            .compareTo(t2.channelStbNumber));
                    break;
            }
            notifyDataSetChanged();
        }

        public List<Channel> getBookmarkedList() {
            return bookmarkedList;
        }
    }

    public static class ChannelViewHolder extends RecyclerView.ViewHolder {
        CheckedTextView textView;

        public ChannelViewHolder(View itemView) {
            super(itemView);
//            int padding = itemView.getResources().getDimensionPixelSize(R.dimen.content_padding);
//            itemView.setPadding(padding, padding, padding, padding);
            textView = ButterKnife.findById(itemView, android.R.id.text1);
        }
    }
}
