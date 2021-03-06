package me.hienngo.astrodemo.domain.repo;

import me.hienngo.astrodemo.model.Channel;
import me.hienngo.astrodemo.model.ChannelDetail;
import me.hienngo.astrodemo.model.ChannelEvent;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author hienngo
 * @since 9/21/17
 */

public interface AstroRepo {
    @GET("ams/v3/getChannelList")
    Observable<Channel.Response> getChannelList();

    @GET("ams/v3/getChannels")
    Observable<ChannelDetail.Response> getChannelsWithMetadata(@Query("channelId") String ids);

    @GET("ams/v3/getEvents")
    Observable<ChannelEvent.Response> getEvents(@Query("channelId") String ids,
                                                @Query("periodStart") String periodStart,
                                                @Query("periodEnd") String periodEnd);
}
