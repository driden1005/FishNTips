package io.driden.fishtips.provider;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
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
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import javax.inject.Inject;

import io.driden.fishtips.app.App;
import io.driden.fishtips.common.CommonUtils;
import io.driden.fishtips.model.RealmLatLng;
import io.driden.fishtips.util.MarkerIconFactory;

public class MapProviderImpl implements MapProvider, ResultCallback {

    private final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String TAG = getClass().getSimpleName();

    @Inject
    DisplayMetrics metrics;
    private LatLng AUCKLAND = new LatLng(-36.839472, 174.770025);
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private GoogleMap googleMap;
    private Activity activity;

    public MapProviderImpl(Activity activity) {
        this.activity = activity;
        App.getAppComponent().inject(this);
    }

    public Projection getProjection() {
        return googleMap.getProjection();
    }

    private GoogleMap initGoogleMap(GoogleMapBuilder builder) {
        assert builder != null;
        this.googleMap = builder.googleMap;
        return builder.googleMap;
    }

    public GoogleMapBuilder mapBuilder(GoogleMap googleMap) {
        return new GoogleMapBuilder(googleMap);
    }

    @Override
    public void initGoogleApiClient(
            GoogleApiClient.ConnectionCallbacks connectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener failedListener) {
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(failedListener)
                .addApi(LocationServices.API).build();
    }

    /**
     * get a colored marker icon by the color name.
     *
     * @param realmLatLng
     * @param colorName
     * @return
     */
    @Override
    public Marker getMarker(RealmLatLng realmLatLng, @NonNull String colorName) {
        if (realmLatLng != null) {
            Marker savedMarker = getMarkerObj(new LatLng(realmLatLng.getLat(), realmLatLng.getLng()), false);

            return changeIcon(savedMarker, colorName);
        }
        return null;
    }

    public Marker changeIcon(Marker marker, @NonNull String colorName) {
        BitmapDescriptor savedMarkerIcons
                = BitmapDescriptorFactory
                .fromBitmap(resizeMapIcons(MarkerIconFactory.getIcon(activity, colorName), 50, 50));
        marker.setIcon(savedMarkerIcons);

        return marker;
    }

    @Override
    public void setMarkersVisible(boolean isVisible, List<Marker> markers) {
        if (markers == null || markers.isEmpty()) {
            return;
        }

        markers.stream().forEach(marker -> marker.setVisible(isVisible));

    }

    @Override
    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    @Override
    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public LocationRequest getLocationRequest() {
        return locationRequest;
    }

    @Override
    public void setMapBoundary(LatLngBounds latLngBounds) {
        googleMap.setLatLngBoundsForCameraTarget(latLngBounds);
    }

    @Override
    public void animateCamera(LatLng latLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void animateZoomTo(LatLng latLng, float zoom) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void animateCamera(LatLng latLng, float zoom1, float zoom2) {
        if (zoom2 < zoom1) {
            zoom1 = MIN_ZOOM;
        }
        if (zoom1 < MIN_ZOOM) {
            zoom1 = MIN_ZOOM;
        }
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(MIN_ZOOM));  // zoom1
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom2));
    }

    private void moveCamera(LatLng latLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void moveCamera(LatLng latLng, float zoom) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    public void setLocationRequest(long updateInterval, long fastestInterval,
                                   int priority, float smallestDisplacement) {
        locationRequest = new LocationRequest().setInterval(updateInterval)
                .setFastestInterval(fastestInterval).setPriority(priority)
                .setSmallestDisplacement(smallestDisplacement);

        setLocationSettingRequest();
    }

    @Override
    public void startLocationUpdate(LocationListener listener) {
        Log.e(TAG, "startLocationUpdate");
        if (locationRequest == null || googleApiClient == null || !googleApiClient.isConnected()) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            CommonUtils.requestUsePermissions(activity, new String[]{LOCATION_PERMISSION}, RESULT_CODE_REQUEST_LOCATION);
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
        if (CommonUtils.checkPermissionGranted(activity, LOCATION_PERMISSION)) {

        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            CommonUtils.requestUsePermissions(activity, new String[]{LOCATION_PERMISSION}, RESULT_CODE_LAST_LOCATION);
        }
        Location lastLocaton = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocaton != null) {
            moveCamera(new LatLng(lastLocaton.getLatitude(), lastLocaton.getLongitude()));
        } else {
            moveCamera(AUCKLAND);
        }

    }

    @Override
    public Location getLastLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            CommonUtils.requestUsePermissions(activity, new String[]{LOCATION_PERMISSION}, RESULT_CODE_LAST_LOCATION);
        }
        return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    private void setLocationSettingRequest() {
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

    /**
     * Add unsaved marker performed by long clicking the map.
     *
     * @param latlng
     * @return
     */
    @Override
    public Marker addUnsavedMarker(LatLng latlng) {
        return getMarkerObj(latlng, true);
    }

    /**
     * Add Saved markers from Database
     *
     * @param latlng
     * @param isVisible
     * @return
     */
    private Marker getMarkerObj(LatLng latlng, boolean isVisible) {
        return googleMap.addMarker(new MarkerOptions()
                .position(latlng)
                .draggable(false)
                .visible(isVisible));
    }

    @Override
    public void setMapPadding(int i1, int i2, int i3, int i4) {
        googleMap.setPadding(i1, i2, i3, i4);
    }

    @Override
    public void removeMarker(Marker marker) {
        marker.remove();
    }

    private Bitmap resizeMapIcons(int icon, int widthDp, int heightDp) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(activity.getResources(), icon);
        return resizeMapIcons(imageBitmap, widthDp, heightDp);
    }

    private Bitmap resizeMapIcons(Bitmap icon, int widthDp, int heightDp) {
        int pixelWidth = Math.round(widthDp * metrics.density);
        int pixelHeight = Math.round(heightDp * metrics.density);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(icon, pixelWidth, pixelHeight, false);
        return resizedBitmap;
    }


    public final class GoogleMapBuilder {

        private GoogleMap googleMap;

        private GoogleMapBuilder(GoogleMap googleMap) {
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
