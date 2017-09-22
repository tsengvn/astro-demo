package me.hienngo.astrodemo.model;

import java.util.List;

/**
 * @author hienngo
 * @since 9/21/17
 */

public class Channel {
    public long channelId;
    public String channelTitle;
    public long channelStbNumber;
    public String channelCategory;


    public static class Response extends RawResponse {
        public List<Channel> channels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Channel channel = (Channel) o;

        if (channelId != channel.channelId) return false;
        if (channelStbNumber != channel.channelStbNumber) return false;
        return channelTitle != null ? channelTitle.equals(channel.channelTitle) : channel.channelTitle == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (channelId ^ (channelId >>> 32));
        result = 31 * result + (channelTitle != null ? channelTitle.hashCode() : 0);
        result = 31 * result + (int) (channelStbNumber ^ (channelStbNumber >>> 32));
        return result;
    }
}
