package io.driden.fishtips.presenter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Named;

import io.driden.fishtips.R;
import io.driden.fishtips.app.App;
import io.driden.fishtips.common.CommonUtils;
import io.driden.fishtips.model.FishingData;
import io.driden.fishtips.model.FishingDataArrayParcelable;
import io.driden.fishtips.model.RealmFishingData;
import io.driden.fishtips.service.NetworkService;
import io.driden.fishtips.service.NetworkServiceInterface;
import io.driden.fishtips.service.ServiceInterface;
import io.driden.fishtips.util.MapProvider;
import io.driden.fishtips.view.FishingMapView;
import io.realm.Realm;

public class FishingMapPresenterImpl implements FishingMapPresenter<FishingMapView> {

    private static final float BOTTOM_HEIGHT = 300.0f;
    private final String TAG = getClass().getCanonicalName();

    @Inject
    @Named("network")
    SharedPreferences preferences;
    @Inject
    DisplayMetrics metrics;

    @Inject
    Realm realm;

    FishingMapView view;

    MapProvider mapProvider;

    NetworkServiceInterface networkService;

    private List<Marker> markerList;
    private Marker unsavedMarker;

    private Activity activity;
    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private BottomSheetBehavior behavior;

    private boolean isServiceBound = false;

    private List<RealmFishingData> realmDatas;

    public FishingMapPresenterImpl(Activity activity) {
        this.activity = activity;
        App.getAppComponent().inject(this);
    }


    @Override
    public void attachView(FishingMapView view) {
        this.view = view;
    }

    @Override
    public void detachView(FishingMapView view) {
        this.view = view;
    }

    /**
     * Save the unsaved marker into the Realm DB.
     *
     * @param latLng
     * @param fishingDatas
     */
    @Override
    public void saveMarker(LatLng latLng, FishingData[] fishingDatas) {
//        realmDatas = realm.where(RealmFishingData.class).findAll();
//
//        if (realmDatas != null && realmDatas.size() >= 20) {
//            return;
//        }
//
//        RealmFishingData data = new RealmFishingData();
//        data.setIndex(realmDatas != null ? realmDatas.size() : 0);
//        data.setLat(latLng.latitude);
//        data.setLng(latLng.longitude);
//        data.setFishingDatas(fishingDatas);
//
//        realm.beginTransaction();
//        realm.insertOrUpdate(realmDatas);
//        realm.commitTransaction();
//
//        // todo set title and set tag to indentify it was saved.
////        unsavedMarker.setTitle();
////        unsavedMarker.setTitle();
//
//        markerList.add(unsavedMarker);


    }

    @Override
    public void initGoogleApiClient() {
        mapProvider = MapProvider.getInstance(activity);
        // Set Location API, Location Request
        if (CommonUtils.checkPlayService(activity.getApplicationContext())) {
            // Google API
            mapProvider.initGoogleApiClient(this, this);
            // PlayService is enabled, then it has the api client object.
            googleApiClient = mapProvider.getGoogleApiClient();
        } else {
            Log.d(TAG, "initGoogleApiClient: No Google Play Service");
        }
    }

    @Override
    public void setLocationRequest() {
        // Location Request
        mapProvider.setLocationRequest(10000, 5000, LocationRequest.PRIORITY_HIGH_ACCURACY, 20);
    }

    @Override
    public void setGoogleMapConfiguration(GoogleMap googleMap) {
        this.googleMap = mapProvider.mapBuilder(googleMap).defaultBuild();
        // Limited in NZ
        mapProvider.setMapBoundary(MapProvider.LATLNG_NEW_ZEALAND);
        mapProvider.moveLastLocation();

    }

    @Override
    public boolean isMapConnected() {
        if (googleApiClient != null) {
            return googleApiClient.isConnected();
        }
        return false;
    }

    @Override
    public void connectMap() {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void disconnectMap() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void startLocationUpdate() {
        if (googleApiClient != null) {
            mapProvider.startLocationUpdate(this);
        }
    }

    @Override
    public void stopLocationUpdate() {
        if (googleApiClient != null) {
            mapProvider.stopLocationUpdate(this);
        }
    }

    @Override
    public boolean isMyLocaitonEnabled() {
        return googleMap.isMyLocationEnabled();
    }

    @Override
    public void animateMyLastLocation() {
        if (mapProvider.getLastLocation() != null) {
            mapProvider.animateCamera(new LatLng(mapProvider.getLastLocation().getLatitude(),
                    mapProvider.getLastLocation().getLongitude()));
        }
    }

    @Override
    public void addUnsavedMarker(LatLng latlng) {
        if (getBottomBehavior().getState() == BottomSheetBehavior.STATE_HIDDEN) {
            setBottomState(BottomSheetBehavior.STATE_COLLAPSED);

            unsavedMarker = googleMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .draggable(false)
                    .title("Title Here!")
                    .visible(true));

            centerTheMarker(latlng);

            googleMap.setPadding(0, 0, 0, behavior.getPeekHeight());

            // Information for the bottom sheet
            getMarkerInfo(latlng, 2, new Date(), TimeZone.getDefault());
        }
    }

    @Override
    public void removeUnsavedMarker(final Marker marker, boolean isHidden) {
        if (marker != null) {
            view.removeMaker(marker);
            googleMap.setPadding(0, 0, 0, 0);
        }
        if (isHidden) {
            setBottomState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    /**
     * After long clicking, the bottom sheet is up, and the marker centers in the remained map screen.
     *
     * @param latLng
     */
    void centerTheMarker(final LatLng latLng) {
//        mapProvider.animateZoomTo(latLng, MapProvider.MAX_ZOOM);
        Projection proj = googleMap.getProjection();
        Point point = proj.toScreenLocation(latLng);
        int halfX = metrics.widthPixels / 2;
        int halfY = metrics.heightPixels / 2;
        int offsetX = halfX + (point.x - halfX);
        int offsetY = halfY + (point.y - halfY) + ((metrics.heightPixels - (int) (50 * metrics.density)) - behavior.getPeekHeight()) / 2;
        LatLng centerLatLang = proj.fromScreenLocation(new Point(offsetX, offsetY));
        mapProvider.animateCamera(centerLatLang);
    }

    @Override
    public void connectService() {
        Intent intent = new Intent(activity, NetworkService.class);
        activity.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void disconnectService() {
        if (isServiceBound) {
            activity.unbindService(connection);
            isServiceBound = false;
        }
    }

    /**
     * Get Information relating the marker
     *
     * @param latLng
     * @param day
     * @param date
     * @param timeZone
     */
    private void getMarkerInfo(final LatLng latLng, int day, Date date, TimeZone timeZone) {

        ServiceInterface.ServiceCallback callback = new ServiceInterface.ServiceCallback() {
            @Override
            public void onSuccess(Bundle bundle) {
                view.setLoadingBottom(false);

                FishingDataArrayParcelable parcel = bundle.getParcelable("FISHING_DATA");
                FishingData[] dataArray = parcel.getDataArray();

                // set data into the bottom sheet
                view.addBottomSheetContents(latLng, dataArray);
            }

            @Override
            public void onFailure(Bundle bundle) {
                view.setLoadingBottom(false);
                String message = bundle.getString("MESSAGE");
                view.showToast(message);
                if (unsavedMarker != null) {
                    removeUnsavedMarker(unsavedMarker, true);
                }
            }
        };

        view.setLoadingBottom(true);

        networkService.getFishingInfo(latLng, day, date, timeZone, callback);
    }

    @Override
    public void setBottomSheetBehavior(View bottomSheet) {
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        view.setFabBottomVisibilty(true);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        view.setFabBottomVisibilty(false);
                        removeUnsavedMarker(unsavedMarker, false);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                Log.d("onSlide", Float.toHexString(slideOffset));
            }
        });

        float density = metrics.density;
        float heightDp = BOTTOM_HEIGHT;
        float heightPx = heightDp * density;

        behavior.setPeekHeight(Math.round(heightPx));
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public BottomSheetBehavior getBottomBehavior() {
        return behavior;
    }

    @Override
    public void setBottomState(int state) {
        if (behavior != null) {
            behavior.setState(state);
        }
    }

    private void getCookie() {
        ServiceInterface.ServiceCallback callback = new ServiceInterface.ServiceCallback() {
            @Override
            public void onSuccess(Bundle bundle) {

                String cookieStr;

                if (bundle.getBoolean("HAS_COOKIE", false)) {
                    cookieStr = preferences.getString(activity.getString(R.string.key_cookie), "");
                } else {
                    cookieStr = bundle.getString(activity.getString(R.string.key_cookie), "");

                    if (!"".equals(cookieStr)) {
                        preferences.edit()
                                .putString(activity.getString(R.string.key_cookie), cookieStr)
                                .putLong(activity.getString(R.string.key_cookie_time), new Date().getTime()).commit();
                    }
                }
            }

            @Override
            public void onFailure(Bundle bundle) {

            }
        };

        networkService.getCookie(callback);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        mapProvider.startLocationUpdate(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
        if (!connectionResult.hasResolution()) {
            GoogleApiAvailability.getInstance().getErrorDialog(activity,
                    connectionResult.getErrorCode(), 0).show();

            Log.d(TAG, "onConnectionFailed: getErrorDialog: " + connectionResult.getErrorCode());

            return;
        }
        try {
            connectionResult.startResolutionForResult(activity, connectionResult.getErrorCode());
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    long interval = 0;

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "startLocationUpdate");
        long milisec = new Date().getTime();
        Toast.makeText(activity, "[" + (milisec - interval) + "]" + location.getLatitude() + " / " + location.getLongitude(), Toast.LENGTH_LONG).show();
        interval = milisec;
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NetworkService.NetworkServiceBinder binder = (NetworkService.NetworkServiceBinder) service;
            networkService = binder.getService();
            isServiceBound = true;
            if (!CommonUtils.isNetworkConnected(activity)) {
                return;
            }
            getCookie();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


}
