package me.hienngo.astrodemo.ui.main;

import com.hannesdorfmann.mosby3.mvp.MvpView;

import java.util.List;

import me.hienngo.astrodemo.model.ChannelDetail;

/**
 * @author hienngo
 * @since 9/22/17
 */

public interface MainView extends MvpView{
    public void onReceivedData(List<ChannelDetail> channelDetailList);
}
