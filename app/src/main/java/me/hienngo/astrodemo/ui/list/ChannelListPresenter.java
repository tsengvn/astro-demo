package me.hienngo.astrodemo.ui.list;

import android.util.Pair;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import me.hienngo.astrodemo.domain.interactor.BookmarkManager;
import me.hienngo.astrodemo.domain.interactor.ChannelManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author hienngo
 * @since 9/21/17
 */

class ChannelListPresenter extends MvpBasePresenter<ChannelListView> {
    private final ChannelManager channelManager;
    private final BookmarkManager bookmarkManager;

    public ChannelListPresenter(ChannelManager channelManager, BookmarkManager bookmarkManager) {
        this.channelManager = channelManager;
        this.bookmarkManager = bookmarkManager;
    }

    public void loadChannelData() {
        Observable.combineLatest(channelManager.getChannelList(), bookmarkManager.getBookmarkedChannel(), (channels, bookmarks) -> new Pair<>(channels, bookmarks))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(pair -> getView().setData(pair.first, pair.second), throwable -> Timber.e("error", throwable));

    }
}
