package me.hienngo.astrodemo.domain.repo;

import java.util.List;

import me.hienngo.astrodemo.model.Channel;
import me.hienngo.astrodemo.model.ChannelDetail;
import retrofit2.http.GET;
import rx.Observable;

/**
 * @author hienngo
 * @since 9/21/17
 */

public interface AstroRepo {
    @GET("ams/v3/getChannelList")
    Observable<Channel.Response> getChannelList();

    @GET("ams/v3/getChannels")
    Observable<List<ChannelDetail>> getChannelsWithMetadata();
}
