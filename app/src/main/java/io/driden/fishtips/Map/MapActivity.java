package io.driden.fishtips.Map;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.driden.fishtips.R;
import io.driden.fishtips.common.Enums.PriorityLevel;
import io.driden.fishtips.common.Tasks.FABOnOffAsyncTask;

import static io.driden.fishtips.Map.MapUtil.MIN_ZOOM;
import static io.driden.fishtips.Map.MapUtil.NEW_ZEALAND;
import static io.driden.fishtips.Map.MapUtil.REQUEST_CODE_1;
import static io.driden.fishtips.Map.MapUtil.RESULT_CODE_1;
import static io.driden.fishtips.Map.MapUtil.RESULT_CODE_2;
import static io.driden.fishtips.Map.MapUtil.RESULT_CODE_3;
import static io.driden.fishtips.Map.MapUtil.RESULT_CODE_4;

public class MapActivity extends FragmentActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,

        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener,

        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener,

        LocationListener,
        MapPresenter.View {

    private final String TAG = this.getClass().getSimpleName();

    private MapUtil mapUtil;
    private MapPresenter mapPresenter;
    private FABOnOffAsyncTask task;
    private boolean checkReset = false;

    private HashMap<Integer, Marker> markerMap;

    @BindView(R.id.btn1)
    Button btn1;
    @BindView(R.id.btn2)
    Button btn2;
    @BindView(R.id.btn3)
    Button btn3;
    @BindView(R.id.btn4)
    Button btn4;
    @BindView(R.id.btn5)
    Button btn5;
    @BindView(R.id.fab1)
    FloatingActionButton fabBtn1;
    @BindView(R.id.fab2)
    FloatingActionButton fabBtn2;

    @OnClick(R.id.fab1)
    void clickFab1() {

    }

    @OnClick(R.id.fab2)
    void clickFab2() {
        if (mapUtil.getGoogleMap().isMyLocationEnabled()) {
            Location lastLocation = mapUtil.getLastLocation();
            if (lastLocation != null) {
                mapUtil.animateCamera(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
            }
        }
    }

    @OnClick({R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5})
    void clickViews(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                Log.e(TAG, "btn1");
                mapUtil.animateCamera(MapUtil.AUCKLAND, MIN_ZOOM, 10f);
                break;
            case R.id.btn2:
                Log.e(TAG, "btn2");
                mapUtil.animateCamera(MapUtil.WELLINGTON, MIN_ZOOM, 10f);
                break;
            case R.id.btn3:
                Log.e(TAG, "btn3");
                mapUtil.animateCamera(MapUtil.TEST_LOCATION_1, MIN_ZOOM, 10f);
                break;
            case R.id.btn4:
                Log.e(TAG, "btn4");
                mapUtil.animateCamera(MapUtil.TEST_LOCATION_2, MIN_ZOOM, 10f);
                break;
            case R.id.btn5:
                Log.e(TAG, "btn5");
                mapUtil.moveCamera(MapUtil.TEST_LOCATION_1, 10f);
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        fabBtn1.setFocusableInTouchMode(true);

        mapUtil = MapUtilImpl.getInstance(MapActivity.this);

        mapPresenter = new MapPresenterImpl(MapActivity.this);
        mapPresenter.setView(this);

        // Set Location API, Location Request
        if (mapUtil.checkPlayService()) {
            Log.d(TAG, "checkPlayServices : true");
            mapUtil.setLocationServiceAPI(this, this);
            mapUtil.setLocationRequest(10000, 5000, LocationPriority.setPriority(PriorityLevel.HIGH), 20);

        } else {
            Log.d(TAG, "checkPlayServices : false");
            // Install Google Play Service
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart");
        if (!mapUtil.getGoogleApiClient().isConnected()) {
            mapUtil.getGoogleApiClient().connect();
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        mapUtil.startLocationUpdate(this);
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        mapUtil.stopLocationUpdate(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        if (mapUtil.getGoogleApiClient().isConnected()) {
            mapUtil.getGoogleApiClient().disconnect();
        }
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case RESULT_CODE_1: {
                Log.d(TAG, "RESULT_CODE_1 : " + RESULT_CODE_1);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission is granted. #1", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission is not granted. #1", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case RESULT_CODE_2: {
                Log.d(TAG, "RESULT_CODE_2 : " + RESULT_CODE_2);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission is granted. #2", Toast.LENGTH_SHORT).show();
                    mapUtil.startLocationUpdate(this);
                } else {
                    Toast.makeText(this, "Permission is not granted. #2", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case RESULT_CODE_3: {
                Log.d(TAG, "RESULT_CODE_3 : " + RESULT_CODE_3);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission is granted. #3", Toast.LENGTH_SHORT).show();
                    mapUtil.setGMapProperties();
                } else {
                    Log.e(TAG, "Permission is not granted.");
                    Toast.makeText(this, "Permission is not granted. #3", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case RESULT_CODE_4: {
                Log.d(TAG, "RESULT_CODE_4 : " + RESULT_CODE_4);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission is granted. #4", Toast.LENGTH_SHORT).show();
                    // * Do Something
                } else {
                    Log.e(TAG, "Permission is not granted.");
                    Toast.makeText(this, "Permission is not granted. #4", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "....onMapReady");
        mapUtil.setGoogleMap(googleMap);
        mapUtil.getGoogleMap().setOnMapClickListener(this);
        mapUtil.getGoogleMap().setOnMapLoadedCallback(this);
        mapUtil.getGoogleMap().setOnMapLongClickListener(this);

        mapUtil.getGoogleMap().setOnCameraIdleListener(this);
        mapUtil.getGoogleMap().setOnCameraMoveCanceledListener(this);
        mapUtil.getGoogleMap().setOnCameraMoveListener(this);
        mapUtil.getGoogleMap().setOnCameraMoveStartedListener(this);

        mapUtil.getGoogleMap().setOnMarkerClickListener(this);
        mapUtil.getGoogleMap().setOnMarkerDragListener(this);

        mapUtil.setGMapProperties();
        mapUtil.setMapBoundary(NEW_ZEALAND);

        mapUtil.moveLastLocation();

        initMarkers();


    }

    void initMarkers() {
        markerMap = new HashMap<>();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "....onConnected");
        mapUtil.startLocationUpdate(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mapUtil.getGoogleApiClient().connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
            return;
        }
        try {
            connectionResult.startResolutionForResult(this, connectionResult.getErrorCode());
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "....onLocationChanged");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_1) {
            if (resultCode == RESULT_OK) {
                mapUtil.startLocationUpdate(this);
            } else {
                Toast.makeText(this, "The GPS is off", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapLoaded() {

        Log.e(TAG, "....onMapLoaded");
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.e(TAG, ".....onMapClick");
        toastHere(latLng);

    }

    void toastHere(LatLng latLng) {
        Toast.makeText(this, latLng.toString(), Toast.LENGTH_SHORT).show();
    }

    void addMarker(LatLng latlng) {
        Marker marker = mapUtil.getGoogleMap().addMarker(
                new MarkerOptions()
                        .position(latlng)
                        .draggable(false)
                        .title("+++")
                        .visible(true));

        marker.setTag(1);
        // Database things...
        markerMap.put(null, marker);
    }

    void removeMarker(Marker marker) {
        int idx = (int) marker.getTag();
        // remove things for database
        marker.remove();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        addMarker(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.e(TAG, "....onMarkerClick");
        removeMarker(marker);
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.e(TAG, "....onMarkerDragStart");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.e(TAG, "....onMarkerDrag");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.e(TAG, "....onMarkerDragEnd");
    }

    @Override
    public void onCameraIdle() {
        if (task != null && task.checkHide) {
            task.execute();
        }
    }

    @Override
    public void onCameraMoveCanceled() {
        if (task != null && task.getStatus() == AsyncTask.Status.PENDING) {
            task.execute();
        }
    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {
        if (task != null) {
            switch (task.getStatus()) {
                case PENDING:
                    checkReset = false;
                    break;
                case FINISHED:
                    checkReset = true;

                    break;
                case RUNNING:
                    task.cancel(true);
                    if (task.isCancelled()) {
                        checkReset = true;
                    }
                    break;
                default:
                    break;
            }

        }

        if (checkReset) {
            FABOnOffAsyncTask.resetTask();
        }
        task = FABOnOffAsyncTask.getInstance(fabBtn1, 1000);
        task.checkHide = true;
    }
}
