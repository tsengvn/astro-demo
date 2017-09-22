package me.hienngo.astrodemo.model;

import com.annimon.stream.Stream;

import java.util.List;

/**
 * @author hienngo
 * @since 9/21/17
 */

public class ChannelDetail extends Channel {
    public String channelDescription;
    public List<ChannelExtRef> channelExtRef;

    public static class Response extends RawResponse {
        public List<ChannelDetail> channel;
    }

    public String getLogoUrl() {
        return Stream.of(channelExtRef)
                .filter(ext -> ext.system.equalsIgnoreCase("logo"))
                .findFirst()
                .map(ext -> ext.value)
                .orElse("");
    }
}
