package io.driden.fishtips.dependancy.component;

import javax.inject.Singleton;

import dagger.Component;
import io.driden.fishtips.dependancy.module.AppModule;
import io.driden.fishtips.dependancy.module.DataModule;
import io.driden.fishtips.dependancy.module.NetModule;
import io.driden.fishtips.presenter.FishingMapPresenterImpl;
import io.driden.fishtips.service.NetworkService;
import io.driden.fishtips.tasks.CookieRunner;
import io.driden.fishtips.tasks.MarkerInfoRunner;
import io.driden.fishtips.view.BottomItemView;

@Singleton
@Component(modules = {AppModule.class, DataModule.class, NetModule.class})
public interface AppComponent {
    void inject(FishingMapPresenterImpl presenter);

    void inject(NetworkService networkService);

    void inject(CookieRunner cookieRunner);

    void inject(MarkerInfoRunner markerInfoRunner);

    void inject(BottomItemView bottomItemView);
}
