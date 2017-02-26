package io.driden.fishtips.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

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

import io.driden.fishtips.common.CommonUtils;

public class MapProvider implements ResultCallback {

    private final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;

    public static final int REQUEST_CODE_LOCATION_UPDATE = 12321;

    public static final int RESULT_CODE_LAST_LOCATION = 45832;
    public static final int RESULT_CODE_REQUEST_LOCATION = 45833;
    public static final int RESULT_CODE_LOCATION_PERMISSION = 45834;

    public static final float MAX_ZOOM = 17f;
    public static final float MIN_ZOOM = 5.5f;

    public static LatLngBounds LATLNG_NEW_ZEALAND = new LatLngBounds(new LatLng(-47.750526, 165.110728), new LatLng(-33.880332, 178.968273));
    public LatLng AUCKLAND = new LatLng(-36.839472, 174.770025);
    public LatLng WELLINGTON = new LatLng(-41.285257, 174.781817);
    public LatLng TEST_LOCATION_1 = new LatLng(-42.554966, 173.662141);
    public LatLng TEST_LOCATION_2 = new LatLng(-37.293347, 176.077334);

    private final String TAG = getClass().getCanonicalName();
    private static MapProvider instance;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private GoogleMap googleMap;
    private Activity activity;

    public static MapProvider getInstance(Activity activity) {
        if (instance == null) {
            synchronized (MapProvider.class) {
                if (instance == null) {
                    instance = new MapProvider(activity);
                }
            }
        }
        return instance;
    }

    private GoogleMap initGoogleMap(GoogleMapBuilder builder) {
        assert builder != null;
        this.googleMap = builder.googleMap;
        return builder.googleMap;
    }

    public MapProvider(Activity activity) {
        this.activity = activity;
    }

    public GoogleMapBuilder mapBuilder(GoogleMap googleMap) {
        return new GoogleMapBuilder(activity, googleMap);
    }

    public synchronized void initGoogleApiClient(
            GoogleApiClient.ConnectionCallbacks connectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener failedListener) {
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(failedListener)
                .addApi(LocationServices.API).build();
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public LocationRequest getLocationRequest() {
        return locationRequest;
    }

    public void setMapBoundary(LatLng a, LatLng b) {
        setMapBoundary(new LatLngBounds(a, b));
    }

    public void setMapBoundary(LatLngBounds latLngBounds) {
        googleMap.setLatLngBoundsForCameraTarget(latLngBounds);
    }

    public void animateCamera(LatLng latLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void animateZoomTo(LatLng latLng, float zoom) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void animateCamera(LatLng latLng, float zoom1, float zoom2) {
        if(zoom2 < zoom1){
            zoom1 = MIN_ZOOM;
        }
        if(zoom1 < MIN_ZOOM){
            zoom1 = MIN_ZOOM;
        }
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(MIN_ZOOM));  // zoom1
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom2));
    }

    public void moveCamera(LatLng latLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void moveCamera(LatLng latLng, float zoom) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void setLocationRequest(long updateInterval, long fastestInterval,
                                   int priority, float smallestDisplacement) {
        locationRequest = new LocationRequest().setInterval(updateInterval)
                .setFastestInterval(fastestInterval).setPriority(priority)
                .setSmallestDisplacement(smallestDisplacement);

        setLocationSettingRequest();
    }

    public void startLocationUpdate(LocationListener listener) {
        Log.e(TAG, "startLocationUpdate");
        if (locationRequest == null || googleApiClient == null || !googleApiClient.isConnected()) {
            Log.e(TAG, ".......cannot start update");
            return;
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            CommonUtils.requestUsePermissions(activity, new String[]{LOCATION_PERMISSION}, RESULT_CODE_REQUEST_LOCATION);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, listener);
    }

    public void stopLocationUpdate(LocationListener listener) {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, listener);
        }
    }

    public void moveLastLocation() {
        if (CommonUtils.checkPermissionGranted(activity, LOCATION_PERMISSION)) {

        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            CommonUtils.requestUsePermissions(activity, new String[]{LOCATION_PERMISSION}, RESULT_CODE_LAST_LOCATION);
        }
        Location lastLocaton = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocaton != null) {
            moveCamera(new LatLng(lastLocaton.getLatitude(), lastLocaton.getLongitude()));
            return;
        } else {
            moveCamera(AUCKLAND);
        }

    }

    public Location getLastLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            CommonUtils.requestUsePermissions(activity, new String[]{LOCATION_PERMISSION}, RESULT_CODE_LAST_LOCATION);
        }
        return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    void setLocationSettingRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

//        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        // ResultCallback
        result.setResultCallback(this);
    }

    // setLocationSettingRequest
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
                    status.startResolutionForResult(activity, REQUEST_CODE_LOCATION_UPDATE);
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


    public final class GoogleMapBuilder {

        private GoogleMap googleMap;
        private Context context;

        private GoogleMapBuilder(Context context, GoogleMap googleMap) {
            this.context = context;
            this.googleMap = googleMap;
        }

        public GoogleMap defaultBuild() {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.setMinZoomPreference(MIN_ZOOM);
            googleMap.setMaxZoomPreference(MAX_ZOOM);

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                this.googleMap.setMyLocationEnabled(false);
            } else {
                this.googleMap.setMyLocationEnabled(true);
            }

            return initGoogleMap(this);
        }

        public GoogleMap build() {
            return initGoogleMap(this);
        }

        public GoogleMapBuilder setMyLocationEnable(boolean b) {

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                this.googleMap.setMyLocationEnabled(false);
            } else {
                this.googleMap.setMyLocationEnabled(b);
            }
            return this;
        }

        public GoogleMapBuilder setMyLocationButtonEnabled(boolean b) {
            this.googleMap.getUiSettings().setMyLocationButtonEnabled(b);
            return this;
        }

        public GoogleMapBuilder setZoomControlsEnabled(boolean b) {
            this.googleMap.getUiSettings().setZoomControlsEnabled(b);
            return this;
        }

        public GoogleMapBuilder setCompassEnabled(boolean b) {
            this.googleMap.getUiSettings().setCompassEnabled(b);
            return this;
        }

        public GoogleMapBuilder setMinZoomPreference(int MIN_ZOOM) {
            this.googleMap.setMinZoomPreference(MIN_ZOOM);
            return this;
        }

        public GoogleMapBuilder setMaxZoomPreference(int MAX_ZOOM) {
            this.googleMap.setMaxZoomPreference(MAX_ZOOM);
            return this;
        }

    }
}
