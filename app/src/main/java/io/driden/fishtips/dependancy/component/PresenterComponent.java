package io.driden.fishtips.dependancy.component;

import dagger.Component;
import io.driden.fishtips.dependancy.module.PresenterModule;
import io.driden.fishtips.scope.PerActivity;
import io.driden.fishtips.ui.activity.FishingMapActivity;

@PerActivity
@Component(modules = {PresenterModule.class})
public interface PresenterComponent {
    void inject(FishingMapActivity activity);
}
