package me.hienngo.astrodemo.ui.list;

import com.hannesdorfmann.mosby3.mvp.MvpView;

import java.util.List;

import me.hienngo.astrodemo.model.Channel;

/**
 * @author hienngo
 * @since 9/21/17
 */

interface ChannelListView extends MvpView{
    void setData(List<Channel> channelList, List<Channel> bookmarkList);
}
