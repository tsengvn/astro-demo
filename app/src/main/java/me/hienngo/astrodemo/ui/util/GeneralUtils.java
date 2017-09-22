package me.hienngo.astrodemo.ui.util;

import android.support.v7.widget.RecyclerView;

import com.annimon.stream.Stream;

/**
 * @author hienngo
 * @since 9/22/17
 */

public class GeneralUtils {
    public static void syncVerticalScroll(RecyclerView... recyclerViews){
        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Stream.of(recyclerViews)
                        .filter(other -> other != recyclerView)
                        .forEach(other -> {
                            other.removeOnScrollListener(this);
                            other.scrollBy(0, dy);
                            other.addOnScrollListener(this);
                        });
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Stream.of(recyclerViews)
                            .filter(other -> other != recyclerView)
                            .forEach(other -> {
                                other.removeOnScrollListener(this);
                                other.setScrollY(recyclerView.getScrollY());
                                other.addOnScrollListener(this);
                            });
                }
            }
        };
        Stream.of(recyclerViews).forEach(recyclerView -> recyclerView.addOnScrollListener(scrollListener));
    }

    public static void syncHorizontalScroll(RecyclerView... recyclerViews){
        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Stream.of(recyclerViews)
                        .filter(other -> other != recyclerView)
                        .forEach(other -> other.scrollBy(dx, 0));
            }
        };
        Stream.of(recyclerViews).forEach(recyclerView -> recyclerView.addOnScrollListener(scrollListener));
    }
}
