package io.driden.fishtips.Map;

import android.app.Activity;

/**
 * Created by driden on 16/01/2017.
 */

public class MapPresenterImpl implements MapPresenter {
    private MapPresenter.View view;
    private Activity activity;

    public MapPresenterImpl(Activity activity){
        this.activity = activity;
    }

    @Override
    public void setView(MapPresenter.View view) {
        this.view = view;
    }
}
