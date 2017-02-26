package io.driden.fishtips.dependancy.module;

import android.app.Application;
import android.util.DisplayMetrics;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    DisplayMetrics provideMetrics(Application application) {
        return application.getResources().getDisplayMetrics();
    }
}
