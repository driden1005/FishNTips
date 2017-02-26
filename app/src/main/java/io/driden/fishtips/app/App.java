package io.driden.fishtips.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.driden.fishtips.R;
import io.driden.fishtips.dependancy.component.AppComponent;
import io.driden.fishtips.dependancy.component.DaggerAppComponent;
import io.driden.fishtips.dependancy.component.DaggerPresenterComponent;
import io.driden.fishtips.dependancy.component.PresenterComponent;
import io.driden.fishtips.dependancy.module.AppModule;
import io.driden.fishtips.dependancy.module.DataModule;
import io.driden.fishtips.dependancy.module.NetModule;
import io.driden.fishtips.dependancy.module.PresenterModule;
import io.fabric.sdk.android.Fabric;

public class App extends Application {

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    static AppComponent appComponent;
    PresenterComponent presenterComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .dataModule(new DataModule(this))
                .netModule(new NetModule(getString(R.string.url_bite_times))).build();
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    public PresenterComponent getPresenterComponent(Activity activity) {
        presenterComponent = DaggerPresenterComponent.builder().presenterModule(new PresenterModule(activity)).build();
        return presenterComponent;
    }
}
