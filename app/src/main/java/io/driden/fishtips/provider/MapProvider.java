package io.driden.fishtips.provider;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import io.driden.fishtips.model.RealmLatLng;

public interface MapProvider {

    int REQUEST_CODE_LOCATION_UPDATE = 12321;
    int RESULT_CODE_LAST_LOCATION = 45832;
    int RESULT_CODE_REQUEST_LOCATION = 45833;
    int RESULT_CODE_LOCATION_PERMISSION = 45834;

    float MAX_ZOOM = 17f;
    float MIN_ZOOM = 5.5f;

    LatLngBounds LATLNG_NEW_ZEALAND = new LatLngBounds(new LatLng(-47.750526, 165.110728), new LatLng(-33.880332, 178.968273));

    Marker getMarker(RealmLatLng realmlatLng, String maj1color);

    void setMarkersVisible(boolean b, List<Marker> markers);

    void initGoogleApiClient(GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                             GoogleApiClient.OnConnectionFailedListener failedListener);

    GoogleApiClient getGoogleApiClient();

    void setLocationRequest(long updateInterval, long fastestInterval,
                            int priority, float smallestDisplacement);

    MapProviderImpl.GoogleMapBuilder mapBuilder(GoogleMap googleMap);

    void setMapBoundary(LatLngBounds latLngBounds);

    void moveLastLocation();

    Marker changeIcon(Marker marker, String colorName);

    void removeMarker(Marker marker);

    void setMapPadding(int i1, int i2, int i3, int i4);

    void startLocationUpdate(LocationListener listener);

    void stopLocationUpdate(LocationListener listener);

    GoogleMap getGoogleMap();

    Location getLastLocation();

    void animateCamera(LatLng latLng);

    Marker addUnsavedMarker(LatLng latlng);

    Projection getProjection();
}
