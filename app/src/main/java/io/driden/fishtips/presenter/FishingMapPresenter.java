package io.driden.fishtips.presenter;

import android.support.design.widget.BottomSheetBehavior;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import io.driden.fishtips.model.FishingData;

public interface FishingMapPresenter<T> extends
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    void initGoogleApiClient();

    void setLocationRequest();

    void setGoogleMapConfiguration(GoogleMap googleMap);

    boolean isMapConnected();

    void connectMap();

    void disconnectMap();

    void startLocationUpdate();

    void stopLocationUpdate();

    void addUnsavedMarker(LatLng latlng);

    void connectService();

    void disconnectService();

    void setBottomSheetBehavior(android.view.View bottomSheet);

    BottomSheetBehavior getBottomBehavior();

    boolean isMyLocaitonEnabled();

    void animateMyLastLocation();

    void getSavedMarkers();

    void attachView(T view);

    void detachView(T view);

    void saveMarker(LatLng latLng, FishingData[] fishingDatas);

    void removeSelectedMarker(Marker marker, boolean isHidden);

    void removeSavedMarker(LatLng latLng);

    void getSavedFishingData(Marker marker);

    void updateIconColors();
}
