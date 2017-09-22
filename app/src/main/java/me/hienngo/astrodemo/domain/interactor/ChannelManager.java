package me.hienngo.astrodemo.domain.interactor;

import java.util.List;

import me.hienngo.astrodemo.domain.repo.AstroRepo;
import me.hienngo.astrodemo.model.Channel;
import rx.Observable;

/**
 * @author hienngo
 * @since 9/21/17
 */

public class ChannelManager {
    private final AstroRepo astroRepo;

    public ChannelManager(AstroRepo astroRepo) {
        this.astroRepo = astroRepo;
    }

    public Observable<List<Channel>> getChannelList() {
        return astroRepo.getChannelList().map(response -> response.channels);
    }

}
