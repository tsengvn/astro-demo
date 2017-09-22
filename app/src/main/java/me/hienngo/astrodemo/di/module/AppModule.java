package me.hienngo.astrodemo.di.module;

import android.content.Context;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.hienngo.astrodemo.domain.interactor.BookmarkManager;
import me.hienngo.astrodemo.domain.interactor.ChannelManager;
import me.hienngo.astrodemo.domain.repo.AstroRepo;

/**
 * @author hienngo
 * @since 9/21/17
 */
@Module
public class AppModule {
    private final Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Singleton @Provides
    public Context provideContext() {
        return this.context;
    }

    @Singleton @Provides
    public ChannelManager provideChannelManager(AstroRepo astroRepo) {
        return new ChannelManager(astroRepo);
    }

    @Singleton @Provides
    public Gson provideGson() {
        return new Gson();
    }

    @Singleton @Provides
    public BookmarkManager provideBookmarkManager(Gson gson, Context context) {
        return new BookmarkManager(gson, context);
    }
}
