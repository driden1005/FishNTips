package io.driden.fishtips.presenter;

import android.support.design.widget.BottomSheetBehavior;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

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

    void removeUnsavedMarker(Marker marker, boolean isHidden);

    void connectService();

    void disconnectService();

    void setBottomSheetBehavior(android.view.View bottomSheet);

    BottomSheetBehavior getBottomBehavior();

    void setBottomState(int state);

    boolean isMyLocaitonEnabled();

    void animateMyLastLocation();

    void attachView(T view);

    void detachView(T view);
}
