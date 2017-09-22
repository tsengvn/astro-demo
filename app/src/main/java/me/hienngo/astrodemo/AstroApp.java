package me.hienngo.astrodemo;

import android.app.Application;

import me.hienngo.astrodemo.di.component.AppComponent;
import me.hienngo.astrodemo.di.component.DaggerAppComponent;
import me.hienngo.astrodemo.di.module.AppModule;
import me.hienngo.astrodemo.di.module.NetworkModule;
import timber.log.Timber;

/**
 * @author hienngo
 * @since 9/21/17
 */

public class AstroApp extends Application {
    private AppComponent appComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule())
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
