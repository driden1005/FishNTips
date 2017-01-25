package io.driden.fishtips.Map;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import io.driden.fishtips.common.Utils.CommonUtils;

import static com.google.android.gms.common.ConnectionResult.SUCCESS;

/**
 * Created by driden on 16/01/2017.
 */

public class MapUtilImpl implements MapUtil, ResultCallback {

    private final String TAG = getClass().getSimpleName();
    private Activity activity;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationManager locationManager;
    private static MapUtilImpl instance;
    private GoogleMap googleMap;

    private MapUtilImpl(Activity activity) {
        this.activity = activity;
    }

    public static MapUtilImpl getInstance(Activity activity) {
        if (instance == null) {
            synchronized (MapUtilImpl.class) {
                if (instance == null) {
                    instance = new MapUtilImpl(activity);
                }
            }
        }
        return instance;
    }

    public boolean checkPlayService() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (SUCCESS != resultCode) {
            return false;
        }
        return true;
    }

    @Override
    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public synchronized void setLocationServiceAPI(GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener failedListener) {
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(failedListener)
                .addApi(LocationServices.API)
                .build();
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    @Override
    public LocationRequest getLocationRequest() {
        return locationRequest;
    }

    @Override
    public void setGMapProperties() {
        if (CommonUtils.checkPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            CommonUtils.requestUsePermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, RESULT_CODE_3);
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.setMinZoomPreference(MIN_ZOOM);
        googleMap.setMaxZoomPreference(MAX_ZOOM);
    }

    @Override
    public void setMapBoundary(LatLng a, LatLng b) {

        Log.e(TAG, "setMapBoundary");
        setMapBoundary(new LatLngBounds(a, b));
    }

    @Override
    public void setMapBoundary(LatLngBounds latLngBounds) {
        googleMap.setLatLngBoundsForCameraTarget(latLngBounds);
    }

    @Override
    public void animateCamera(LatLng latLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void animateCamera(LatLng latLng, float zoom1, float zoom2) {
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(MIN_ZOOM));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f));
    }

    @Override
    public void moveCamera(LatLng latLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void moveCamera(LatLng latLng, float zoom) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    public void setLocationRequest(long updateInterval, long fastestInterval,
                                   int priority, float smallestDisplacement) {
        locationRequest = new LocationRequest()
                .setInterval(updateInterval)
                .setFastestInterval(fastestInterval)
                .setPriority(priority)
                .setSmallestDisplacement(smallestDisplacement);

        setRequest();
    }

    @Override
    public void startLocationUpdate(LocationListener listener) {
        Log.e(TAG, "startLocationUpdate");
        if (locationRequest == null || googleApiClient == null || !googleApiClient.isConnected()) {
            Log.e(TAG, ".......cannot start update");
            return;
        }
        if (CommonUtils.checkPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            CommonUtils.requestUsePermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, RESULT_CODE_2);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, listener);
    }

    @Override
    public void stopLocationUpdate(LocationListener listener) {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, listener);
        }
    }

    @Override
    public void moveLastLocation() {
        if (CommonUtils.checkPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            CommonUtils.requestUsePermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, RESULT_CODE_1);
        }
        Location lastLocaton = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocaton != null) {
            moveCamera(new LatLng(lastLocaton.getLatitude(), lastLocaton.getLongitude()));
            return;
        } else {
            moveCamera(AUCKLAND);
        }

    }

    @Override
    public Location getLastLocation() {
        if (CommonUtils.checkPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            CommonUtils.requestUsePermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, RESULT_CODE_1);
        }
        return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    void setRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

//        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull Result result) {
        final Status status = result.getStatus();

        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:

                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(activity, REQUEST_CODE_1);
                } catch (IntentSender.SendIntentException e) {
                    // Ignore the error.
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are not satisfied. However, we have no way to fix the
                // settings so we won't show the dialog.
                break;
        }
    }
}
