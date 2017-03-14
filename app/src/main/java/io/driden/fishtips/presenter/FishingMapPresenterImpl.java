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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import io.driden.fishtips.R;
import io.driden.fishtips.app.App;
import io.driden.fishtips.common.CommonUtils;
import io.driden.fishtips.model.FishingData;
import io.driden.fishtips.model.FishingDataArrayParcelable;
import io.driden.fishtips.model.RealmFishingData;
import io.driden.fishtips.model.RealmLatLng;
import io.driden.fishtips.provider.MapProviderImpl;
import io.driden.fishtips.service.NetworkService;
import io.driden.fishtips.service.NetworkServiceInterface;
import io.driden.fishtips.service.ServiceInterface;
import io.driden.fishtips.view.FishingMapView;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class FishingMapPresenterImpl implements FishingMapPresenter<FishingMapView> {

    private final String TAG = getClass().getCanonicalName();

    private final float BOTTOM_HEIGHT = 300.0f;  // the height of the bottom sheetview when it is collapesed.

    @Inject
    @Named("network")
    SharedPreferences preferences;
    @Inject
    DisplayMetrics metrics;
    @Inject
    Realm realm;

    private MapProviderImpl mapProvider;

    private NetworkServiceInterface networkService;

    private ExecutorService executor;

    private FishingMapView view;

    private long interval = 0;

    private Marker unsavedMarker;

    private List<Marker> savedMarkers;

    private RealmResults<RealmLatLng> savedLatLngs;

    private Activity activity;

    private GoogleApiClient googleApiClient;

    private BottomSheetBehavior behavior;

    private boolean isServiceBound = false;
    // for bounding the service
    private ServiceConnection connection = new ServiceConnection() {
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
            if (executor != null && !executor.isTerminated() && !executor.isShutdown()) {
                executor.shutdownNow();
            }
        }
    };

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
     * instantiate the Google API client.
     */
    @Override
    public void initGoogleApiClient() {
        mapProvider = MapProviderImpl.getInstance(activity);
        // Set Location API, Location Request
        if (CommonUtils.checkPlayService(activity.getApplicationContext())) {
            // Google API
            mapProvider.initGoogleApiClient(this, this);
            // PlayService is enabled, then it has the api client object.
            googleApiClient = mapProvider.getGoogleApiClient();
        } else {
            Log.d(TAG, "initGoogleApiClient: No Google Play Service found.");
        }
    }

    /**
     * Set the location request (update interval, range to update, priory)
     */
    @Override
    public void setLocationRequest() {
        // Location Request
        mapProvider.setLocationRequest(10000, 5000, LocationRequest.PRIORITY_HIGH_ACCURACY, 20);
    }

    /**
     * Init the google Map with configuration (get the GoogleMap from the activity)
     *
     * @param googleMap
     */
    @Override
    public void setGoogleMapConfiguration(GoogleMap googleMap) {
        mapProvider.mapBuilder(googleMap).defaultBuild();
        // Limited in NZ
        mapProvider.setMapBoundary(MapProviderImpl.LATLNG_NEW_ZEALAND);
        mapProvider.moveLastLocation();
    }

    /**
     * get the list of saved markers
     */
    @Override
    public void getSavedMarkers() {
        RealmResults<RealmLatLng> savedLatLngs = realm.where(RealmLatLng.class).findAll();

        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM");
        String currentDateStr = sdf.format(currentDate);

        savedMarkers = new ArrayList<>();

        Observable.range(0, savedLatLngs.size())
                .subscribe(i -> {
                    RealmFishingData data =
                            realm.where(RealmFishingData.class)
                                    .equalTo("date", currentDateStr)
                                    .equalTo("milisec", savedLatLngs.get(i).getMilisec())
                                    .findFirst();
                    Marker marker = getColoredMarker(currentDate, savedLatLngs.get(i), data);
                    marker.setTag(i);
                    savedMarkers.add(marker);
                }, e -> {
                    Log.d(TAG, "getSavedMarkers: " + e.getMessage());
                    if (!savedMarkers.isEmpty()) {
                        setMarkersVisibility(savedMarkers, true);
                    }
                }, () -> setMarkersVisibility(savedMarkers, true));
    }

    /**
     * Update Icons
     */
    public void updateIconColors() {
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM");
        String currentDateStr = sdf.format(currentDate);

        Observable.range(0, savedLatLngs.size())
                .subscribe(i -> {
                            RealmFishingData data =
                                    realm.where(RealmFishingData.class)
                                            .equalTo("date", currentDateStr)
                                            .equalTo("milisec", savedLatLngs.get(i).getMilisec())
                                            .findFirst();

                            checkMarkerIconColors(savedMarkers.get(i), currentDate, data);

                        }, e -> Log.e(TAG, "updateIconColors: " + e.getMessage()),
                        () -> view.showToast("Updating markers has been completed."));
    }

    /**
     * Just check Icon Colors for the current time.
     *
     * @param marker
     * @param currentDate
     * @param data
     * @return
     */
    private Marker checkMarkerIconColors(Marker marker, Date currentDate, RealmFishingData data) {
        if (data != null) {

            if (isTheTimeIncluded(currentDate, data.getMajor1())) {
                marker = mapProvider.changeIcon(marker, data.getMaj1color());
            } else if (isTheTimeIncluded(currentDate, data.getMajor2())) {
                marker = mapProvider.changeIcon(marker, data.getMaj2color());
            } else if (isTheTimeIncluded(currentDate, data.getMinor1())) {
                marker = mapProvider.changeIcon(marker, data.getMin1color());
            } else if (isTheTimeIncluded(currentDate, data.getMinor2())) {
                marker = mapProvider.changeIcon(marker, data.getMin2color());
            } else {
                marker = mapProvider.changeIcon(marker, "");
            }
        } else {
            marker = mapProvider.changeIcon(marker, "");
        }

        return marker;
    }

    /**
     * get Different colors of the markers
     *
     * @param currentDate
     * @param realmlatLng
     * @param data
     * @return
     */
    private Marker getColoredMarker(Date currentDate, RealmLatLng realmlatLng, RealmFishingData data) {

        Marker marker;

        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM");
        String currentDateStr = sdf.format(currentDate);

        if (data != null && currentDateStr.equals(data.getDate())) {

            Log.d(TAG, "getColoredMarker: " + currentDateStr + "___" + data.getDate());

            if (isTheTimeIncluded(currentDate, data.getMajor1())) {
                marker = mapProvider.getMarker(realmlatLng, data.getMaj1color());
            } else if (isTheTimeIncluded(currentDate, data.getMajor2())) {
                marker = mapProvider.getMarker(realmlatLng, data.getMaj2color());
            } else if (isTheTimeIncluded(currentDate, data.getMinor1())) {
                marker = mapProvider.getMarker(realmlatLng, data.getMin1color());
            } else if (isTheTimeIncluded(currentDate, data.getMinor2())) {
                marker = mapProvider.getMarker(realmlatLng, data.getMin2color());
            } else {
                marker = mapProvider.getMarker(realmlatLng, "");
            }

        } else {
            marker = mapProvider.getMarker(realmlatLng, "");
        }

        return marker;
    }

    /**
     * Get true or false if current time is in the major or minor bite time period.
     * this code is terribly ugly.... need to fix it later :(
     *
     * @param currentDate
     * @param dateRange
     * @return
     */
    private boolean isTheTimeIncluded(Date currentDate, String dateRange) {

        if (dateRange.contains("--:--")) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        String currentTimeStr = sdf.format(currentDate);

        final String regex = "[0-9]{2}:[0-9]{2}";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(dateRange);

        Date currentTime = null;
        try {
            currentTime = sdf.parse(currentTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (currentTime == null) {
            return false;
        }

        Date[] times = new Date[2];

        int count = 0;
        while (matcher.find()) {
            try {
                times[count++] = sdf.parse(matcher.group(0));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if ((times[0].getTime() <= currentTime.getTime()) && (times[1].getTime() >= currentTime.getTime())) {
            return true;
        }

        return false;
    }

    /**
     * Save the unsaved marker into the Realm.
     * 1. get saved latlngs
     * 2. get saved fishing data relating to latlngs
     * 3. convert the unsaved data array to the collection.
     * 4. get unsaved latlng and put that into the latlngs collection.
     * 5. insert update latlngs
     * 6. put unsaved data into the saved data collection.
     * 7. insert update fishing data.
     *
     * @param latLng
     * @param fishingDatas
     */
    @Override
    public void saveMarker(LatLng latLng, FishingData[] fishingDatas) {

        savedLatLngs = realm.where(RealmLatLng.class).findAll();

        if (savedLatLngs != null && savedLatLngs.size() >= 20) {
            view.showToast("Max 20 lists had been reached.");
            return;
        }

        // put newly saved marker into Marker collection.
        RealmLatLng unsavedLatLng = new RealmLatLng(fishingDatas);

        RealmList<RealmFishingData> unsavedMarkerDataList = saveFishingData(unsavedLatLng, fishingDatas);

        Date currentDate = new Date();

        Marker marker = getColoredMarker(currentDate, unsavedLatLng, unsavedMarkerDataList.get(0));
        marker.setTag(savedLatLngs.size());

        savedMarkers.add(marker);
        marker.setVisible(true);

        unsavedMarker.setVisible(false);
        mapProvider.removeMarker(unsavedMarker);

        view.showToast("The Marker has been added.");
    }

    /**
     * Save the fishing data for the selected marker.
     *
     * @param latLng
     * @param dataArray
     * @return
     */
    private RealmList<RealmFishingData> saveFishingData(RealmLatLng latLng, FishingData[] dataArray) {

        RealmList<RealmFishingData> dataList = new RealmList<>();

        // Convert the array to the Realm collection.
        Arrays.asList(dataArray).stream()
                .map(fishingData -> new RealmFishingData(fishingData))
                .peek(realmFishingData -> realmFishingData.setMilisec(latLng.getMilisec()))
                .forEach(realmFishingData -> dataList.add(realmFishingData));

        realm.beginTransaction();
        realm.insertOrUpdate(latLng);
        realm.insertOrUpdate(dataList);
        realm.commitTransaction();

        return dataList;
    }

    /**
     * set the saved markers' visibility
     *
     * @param isVisible
     */
    private void setMarkersVisibility(List<Marker> markers, boolean isVisible) {
        mapProvider.setMarkersVisible(true, markers);
    }

    /**
     * when one of saved markers is selected, get the tag, and query information.
     *
     * @param marker
     */
    @Override
    public void getSavedFishingData(Marker marker) {
        // Todo get lat lng by tag, fetch data from DB.
        double lat = marker.getPosition().latitude;
        double lng = marker.getPosition().longitude;

        List<RealmFishingData> datas = realm.where(RealmFishingData.class).equalTo("lat", lat).equalTo("lng", lng).findAll();

        if (getBottomBehavior().getState() == BottomSheetBehavior.STATE_HIDDEN) {
            setBottomState(BottomSheetBehavior.STATE_COLLAPSED);

            centerTheMarker(new LatLng(lat, lng));

            mapProvider.setMapPadding(0, 0, 0, behavior.getPeekHeight());
        }

        FishingData[] dataArray = null;
        try {

            dataArray = datas.stream().map(x -> new FishingData(x)).toArray(FishingData[]::new);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (dataArray != null) {
            if (getBottomBehavior().getState() != BottomSheetBehavior.STATE_HIDDEN) {
                // todo Remove FAB
                view.setFabBottomVisibilty(true);
                // set data into the bottom sheet
                view.addBottomSheetContents(new LatLng(lat, lng), dataArray, false);
            } else {
                view.showToast("Adding BottomViewItems is canceled.");
            }
        } else {
            view.showToast("No data was fetched from the server.");
        }
    }

    /**
     * map connection check
     *
     * @return
     */
    @Override
    public boolean isMapConnected() {
        return googleApiClient != null && googleApiClient.isConnected();
    }

    /**
     * Connect the map with API.
     */
    @Override
    public void connectMap() {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    /**
     * Disconnect the connection between the map and API.
     */
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
        return mapProvider.getGoogleMap().isMyLocationEnabled();
    }

    @Override
    public void animateMyLastLocation() {
        if (mapProvider.getLastLocation() != null) {
            mapProvider.animateCamera(new LatLng(mapProvider.getLastLocation().getLatitude(),
                    mapProvider.getLastLocation().getLongitude()));
        }
    }

    /**
     * Add the unsaved Marker on the Map
     *
     * @param latlng
     */
    @Override
    public void addUnsavedMarker(LatLng latlng) {
        if (getBottomBehavior().getState() == BottomSheetBehavior.STATE_HIDDEN) {
            setBottomState(BottomSheetBehavior.STATE_COLLAPSED);

            unsavedMarker = mapProvider.addUnsavedMarker(latlng);

            centerTheMarker(latlng);

            mapProvider.setMapPadding(0, 0, 0, behavior.getPeekHeight());

            // Information for the bottom sheet
            getMarkerInfo(latlng, 2, new Date(), TimeZone.getDefault());

            view.setFabBottomVisibilty(true);
        }
    }

    /**
     * Remove the unsaved Marker on the map
     *
     * @param marker
     * @param makeHidden
     */
    @Override
    public void removeSelectedMarker(final Marker marker, boolean makeHidden) {
        if (marker != null) {
            // When cancel the network thread by force, it makes exception (not on main thread)
            activity.runOnUiThread(() -> mapProvider.removeMarker(marker));
        }

        activity.runOnUiThread(() -> mapProvider.setMapPadding(0, 0, 0, 0));

        // cancel thread if it is still running.
        if (executor != null && !executor.isTerminated() && !executor.isShutdown()) {
            executor.shutdownNow();
        }

        if (makeHidden) {
            setBottomState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    /**
     * remove the selected marker from the marker list,
     * remove fishing data from the realm DB.
     * re-indexing with the markers' tags
     *
     * @param latLng
     */
    @Override
    public void removeSavedMarker(LatLng latLng) {

        Observable.just(latLng).observeOn().subscribe(ll -> {
            Marker selectedMarker = savedMarkers.stream()
                    .filter(marker -> marker.getPosition().latitude == ll.latitude && marker.getPosition().longitude == ll.longitude)
                    .findFirst().get();
            // remove the marker from the marker list
            savedMarkers.remove(selectedMarker);
            // remove the marker object on the screen
            removeSelectedMarker(selectedMarker, true);
            // remove the marker relating fishing data from the realm DB.
            realm.beginTransaction();
            // delete ReamlmLatLng
            realm.where(RealmLatLng.class)
                    .equalTo("lat", ll.latitude).equalTo("lng", ll.longitude)
                    .findAll().deleteAllFromRealm();
            // delete RealmFishingData
            realm.where(RealmFishingData.class)
                    .equalTo("lat", ll.latitude).equalTo("lng", ll.longitude)
                    .findAll().deleteAllFromRealm();
            realm.commitTransaction();
        }, e -> {
        }, () -> {
            rearrangeMarkers(savedMarkers);
            view.showToast("The marker has been removed.");
        });
    }

    /**
     * set tags with values of indexes.
     *
     * @param markers
     */
    private void rearrangeMarkers(List<Marker> markers) {
        Log.d(TAG, "rearrangeMarkers: " + markers.size());
        Observable.range(0, markers.size())
                .observeOn(Schedulers.io())
                .subscribe(i -> activity.runOnUiThread(() -> markers.get(i).setTag(i)),
                        e -> Log.d(TAG, "rearrangeMarkers: " + e.getMessage()),
                        () -> Log.d(TAG, "rearrangeMarkers: Markers were rearranged..."));
    }

    /**
     * After long clicking, the bottom sheet is up, and the marker centers in the remained map screen.
     *
     * @param latLng
     */
    private void centerTheMarker(final LatLng latLng) {
        Projection proj = mapProvider.getProjection();
        Point point = proj.toScreenLocation(latLng);
        int halfX = metrics.widthPixels / 2;
        int halfY = metrics.heightPixels / 2;
        int offsetX = halfX + (point.x - halfX);
        int offsetY = halfY + (point.y - halfY) + ((metrics.heightPixels - (int) (50 * metrics.density)) - behavior.getPeekHeight()) / 2;

        LatLng centerLatLang = proj.fromScreenLocation(new Point(offsetX, offsetY));
        mapProvider.animateCamera(centerLatLang);
    }

    /**
     * Binding the service. It happens onResume().
     */
    @Override
    public void connectService() {
        Intent intent = new Intent(activity, NetworkService.class);
        activity.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Unbind the service. It happens onPause().
     */
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

                FishingData[] dataArray = null;
                try {
                    dataArray = parcel.getDataArray();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                if (dataArray != null) {
                    if (getBottomBehavior().getState() != BottomSheetBehavior.STATE_HIDDEN) {
                        // set data into the bottom sheet
                        view.addBottomSheetContents(latLng, dataArray, true);
                    } else {
                        view.showToast("Adding BottomViewItems is canceled.");
                    }
                } else {
                    view.showToast("No data was fetched from the server.");
                }
            }

            @Override
            public void onFailure(Bundle bundle) {
                view.setLoadingBottom(false);
                String message = bundle.getString("MESSAGE");
                view.showToast(message);
                removeSelectedMarker(unsavedMarker, true);
            }
        };

        view.setLoadingBottom(true);

        executor = networkService.getFishingInfo(latLng, day, date, timeZone, callback);
    }

    /**
     * Defines the behavior of the bottomSheet.
     *
     * @param bottomSheet
     */
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
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        removeSelectedMarker(unsavedMarker, false);
                        view.setFabBottomVisibilty(false);
                        view.flushBottomSheetContents();
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

    private void setBottomState(int state) {
        if (behavior != null) {
            behavior.setState(state);
        }
    }

    private void getCookie() {
        ServiceInterface.ServiceCallback callback = new ServiceInterface.ServiceCallback() {
            @Override
            public void onSuccess(Bundle bundle) {
                if (!bundle.getBoolean("HAS_COOKIE", false)) {
                    String cookieStr = bundle.getString(activity.getString(R.string.key_cookie), "");
                    if (!"".equals(cookieStr)) {
                        // Save Cookie
                        preferences.edit()
                                .putString(activity.getString(R.string.key_cookie), cookieStr)
                                .putLong(activity.getString(R.string.key_cookie_time), new Date().getTime()).apply();
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

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "startLocationUpdate");
        long milisec = new Date().getTime();
        Toast.makeText(activity, "[" + (milisec - interval) + "]" + location.getLatitude() + " / " + location.getLongitude(), Toast.LENGTH_LONG).show();
        interval = milisec;
    }


}
