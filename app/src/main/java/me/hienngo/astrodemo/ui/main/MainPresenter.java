package me.hienngo.astrodemo.ui.main;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import java.util.List;

import me.hienngo.astrodemo.domain.interactor.BookmarkManager;
import me.hienngo.astrodemo.domain.interactor.ChannelManager;
import me.hienngo.astrodemo.model.ChannelDetail;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author hienngo
 * @since 9/22/17
 */

public class MainPresenter extends MvpBasePresenter<MainView> {
    private final ChannelManager channelManager;
    private final BookmarkManager bookmarkManager;
    private Subscription subscription;
    public MainPresenter(ChannelManager channelManager, BookmarkManager bookmarkManager) {
        this.channelManager = channelManager;
        this.bookmarkManager = bookmarkManager;
    }

    public void loadData() {
        subscription = bookmarkManager.getBookmarkedChannel()
                .map(list -> Stream.of(list).map(channel -> channel.channelId).collect(Collectors.toList()))
                .observeOn(Schedulers.io())
                .flatMap(channelManager::getChannelsDetail)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(channelDetails -> getView().onReceivedChannelData(channelDetails),
                        throwable -> {
                            Timber.e(throwable, "error in main presenter ");
                            throwable.printStackTrace();
                        },
                        () -> Timber.i("done"));

    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    public void loadEvent(final List<ChannelDetail> list) {
        final long originTime = System.currentTimeMillis();
        loadEvent(list, originTime, false);
    }

    public void loadEvent(final List<ChannelDetail> list, final long originTime, final boolean loadMore) {
        List<Long> idList = Stream.of(list).map(channel -> channel.channelId).collect(Collectors.toList());
        getView().showLoading();
        channelManager.getEventCalendarIn24h(idList, originTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataMap -> {
                    getView().onReceivedCalendarData(dataMap, originTime, loadMore);
                    getView().hideLoading();
                });
    }
}
