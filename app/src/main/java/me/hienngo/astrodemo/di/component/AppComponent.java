package me.hienngo.astrodemo.di.component;

/**
 * @author hienngo
 * @since 9/21/17
 */

import javax.inject.Singleton;

import dagger.Component;
import me.hienngo.astrodemo.di.module.AppModule;
import me.hienngo.astrodemo.di.module.NetworkModule;
import me.hienngo.astrodemo.ui.list.ChannelListActivity;
import me.hienngo.astrodemo.ui.main.MainActivity;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface AppComponent {
    void inject(ChannelListActivity channelListActivity);

    void inject(MainActivity mainActivity);
}
