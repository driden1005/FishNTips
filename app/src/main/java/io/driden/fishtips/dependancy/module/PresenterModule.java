package io.driden.fishtips.dependancy.module;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;
import io.driden.fishtips.presenter.FishingMapPresenter;
import io.driden.fishtips.presenter.FishingMapPresenterImpl;
import io.driden.fishtips.scope.PerActivity;
import io.driden.fishtips.view.FishingMapView;

@Module
public class PresenterModule {

    Activity activity;

    public PresenterModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    FishingMapPresenter<FishingMapView> provideMapPresenter() {
        return new FishingMapPresenterImpl(activity);
    }
}
