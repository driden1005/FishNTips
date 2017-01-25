package io.driden.fishtips.Map;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by driden on 16/01/2017.
 */

public interface MapUtil {

    int RESULT_CODE_1 = 45832;
    int RESULT_CODE_2 = 45833;
    int RESULT_CODE_3 = 45834;
    int RESULT_CODE_4 = 45835;

    int REQUEST_CODE_1 = 12321;

    float MAX_ZOOM = 17f;
    float MIN_ZOOM = 5.5f;

    LatLngBounds NEW_ZEALAND = new LatLngBounds(new LatLng(-47.750526, 165.110728), new LatLng(-33.880332, 178.968273));
    LatLng AUCKLAND = new LatLng(-36.839472, 174.770025);
    LatLng WELLINGTON = new LatLng(-41.285257, 174.781817);
    LatLng TEST_LOCATION_1 = new LatLng(-42.554966, 173.662141);
    LatLng TEST_LOCATION_2 = new LatLng(-37.293347, 176.077334);

    boolean checkPlayService();

    void setGoogleMap(GoogleMap googleMap);

    GoogleMap getGoogleMap();

    void setLocationServiceAPI(GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener failedListener);

    GoogleApiClient getGoogleApiClient();

    void setLocationRequest(long updateInterval, long fastestInterval, int priority, float smallestDisplacement);

    void startLocationUpdate(LocationListener listener);

    void stopLocationUpdate(LocationListener listener);

    void moveLastLocation();

    LocationRequest getLocationRequest();

    Location getLastLocation();

    void setGMapProperties();

    void setMapBoundary(LatLng a, LatLng b);

    void setMapBoundary(LatLngBounds latLngBounds);

    void animateCamera(LatLng latLng);

    void animateCamera(LatLng latLng, float zoom1, float zoom2);

    void moveCamera(LatLng latLng);

    void moveCamera(LatLng latLng, float zoom);
}
