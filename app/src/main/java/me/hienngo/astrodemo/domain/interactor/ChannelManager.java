package me.hienngo.astrodemo.domain.interactor;

import com.annimon.stream.Stream;

import java.util.List;

import me.hienngo.astrodemo.domain.repo.AstroRepo;
import me.hienngo.astrodemo.model.Channel;
import me.hienngo.astrodemo.model.ChannelDetail;
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

    public Observable<List<ChannelDetail>> getChannelsDetail(List<Long> ids) {
        String idsArray = Stream.of(ids).map(String::valueOf).reduce((id1, id2) -> id1 + "," + id2).orElse("");
        return astroRepo.getChannelsWithMetadata(idsArray).map(response -> response.channel);
    }

}
