package io.driden.fishtips.Loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import io.driden.fishtips.provider.MapProvider;
import io.driden.fishtips.provider.MarkerProvider;
import io.realm.Realm;


public class MarkerLoader extends AsyncTaskLoader<ArrayList<Marker>> {

    private final String TAG = getClass().getSimpleName();
    Realm realm;
    MapProvider mapProvider;
    MarkerProvider markerProvider;

    public MarkerLoader(Context context, Realm realm, MapProvider mapProvider, MarkerProvider markerProvider) {
        super(context);
        this.realm = realm;
        this.mapProvider = mapProvider;
        this.markerProvider = markerProvider;
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading: ");
        super.onStartLoading();
    }

    @Override
    protected void onStopLoading() {
        Log.d(TAG, "onStopLoading: ");
        super.onStopLoading();
    }

    @Override
    public ArrayList<Marker> loadInBackground() {
        Log.d(TAG, "loadInBackground: ");
        return markerProvider.getSavedMarkers(mapProvider);
    }

    @Override
    protected boolean onCancelLoad() {
        Log.d(TAG, "onCancelLoad: ");
        return super.onCancelLoad();
    }

    @Override
    protected void onReset() {
        Log.d(TAG, "onReset: ");
        super.onReset();
    }
}
