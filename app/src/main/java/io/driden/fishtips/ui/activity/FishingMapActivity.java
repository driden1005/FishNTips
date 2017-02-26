package io.driden.fishtips.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.driden.fishtips.R;
import io.driden.fishtips.app.App;
import io.driden.fishtips.model.FishingData;
import io.driden.fishtips.presenter.FishingMapPresenter;
import io.driden.fishtips.service.NetworkService;
import io.driden.fishtips.util.MainThreadSpec;
import io.driden.fishtips.util.MapProvider;
import io.driden.fishtips.view.FishingMapView;
import me.panavtec.threaddecoratedview.views.ViewInjector;

public class FishingMapActivity extends BaseActivity implements FishingMapView
        , OnMapReadyCallback
        , GoogleMap.OnMapClickListener
        , GoogleMap.OnMapLongClickListener
        , GoogleMap.OnMapLoadedCallback
        , GoogleMap.OnMarkerClickListener
        , GoogleMap.OnMarkerDragListener
        , GoogleMap.OnCameraMoveStartedListener
        , GoogleMap.OnCameraMoveCanceledListener
        , GoogleMap.OnCameraMoveListener
        , GoogleMap.OnCameraIdleListener {

    final String TAG = getClass().getCanonicalName();

    @BindView(R.id.fabSaveBtn)
    FloatingActionButton fabSaveBtn;

    @BindView(R.id.coordinator)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.design_bottom_sheet)
    View bottomSheet;

    @BindView(R.id.itemContainer)
    View itemContainer;

    @Inject
    FishingMapPresenter<FishingMapView> presenter;

    MainThreadSpec threadSpec;

    private SupportMapFragment mapFragment;

    /**
     * Gogoole API Client -> Location Request -> Google Map
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_map_fishing, null);
        viewTag = (String) view.getTag();
        setContentView(view);

        Intent intent = new Intent(this, NetworkService.class);
        startService(intent);

        bindView();
        initView();

        getComponent();

        threadSpec = new MainThreadSpec();

        presenter.attachView(ViewInjector.inject(this, threadSpec));
        // BottomSheet
        presenter.setBottomSheetBehavior(bottomSheet);
        presenter.initGoogleApiClient();
        presenter.setLocationRequest();
    }

    @Override
    void getComponent() {
        App.get(this).getPresenterComponent(this).inject(this);
    }

    @Override
    public void initView() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        // OnMapReadyCallback
        mapFragment.getMapAsync(this);
    }


    /**
     * Add Listeners for Google Map
     *
     * @param gMap Google Map
     */
    public void setGoogleMapListeners(GoogleMap gMap) {
        // GoogleMap.OnMapClickListener,
        gMap.setOnMapClickListener(this);
        // GoogleMap.OnMapLongClickListener,
        gMap.setOnMapLongClickListener(this);
        // GoogleMap.OnMapLoadedCallback
        gMap.setOnMapLoadedCallback(this);

        // GoogleMap.OnMarkerClickListener
        gMap.setOnMarkerClickListener(this);
        // GoogleMap.OnMarkerDragListener
        gMap.setOnMarkerDragListener(this);

        // GoogleMap.OnCameraIdleListener
        gMap.setOnCameraIdleListener(this);
        //GoogleMap.OnCameraMoveStartedListener
        gMap.setOnCameraMoveStartedListener(this);
        // GoogleMap.OnCameraMoveListener
        gMap.setOnCameraMoveListener(this);
        // GoogleMap.OnCameraMoveCanceledListener
        gMap.setOnCameraMoveCanceledListener(this);
    }

    @Override
    public void addBottomSheetContents(FishingData[] dataArray) {

        for (int i = 0; i < dataArray.length; i++) {

            FishingData data = dataArray[i];

            View infoItem = LayoutInflater.from(this).inflate(R.layout.bite_info_item, null);
            TextView infoDate = ButterKnife.findById(infoItem, R.id.infoDate);
            TextView infoSunrise = ButterKnife.findById(infoItem, R.id.infoSunrise);
            TextView infoSunset = ButterKnife.findById(infoItem, R.id.infoSunset);
            TextView infoMoonrise = ButterKnife.findById(infoItem, R.id.infoMoonrise);
            TextView infoMoonset = ButterKnife.findById(infoItem, R.id.infoMoonset);
            TextView infoMajor1 = ButterKnife.findById(infoItem, R.id.infoMajor1);
            TextView infoMajor2 = ButterKnife.findById(infoItem, R.id.infoMajor2);
            TextView infoMinor1 = ButterKnife.findById(infoItem, R.id.infoMinor1);
            TextView infoMinor2 = ButterKnife.findById(infoItem, R.id.infoMinor2);
            TextView infoTideStation = ButterKnife.findById(infoItem, R.id.infoTideStation);
            TextView infoTide1 = ButterKnife.findById(infoItem, R.id.infoTide1);
            TextView infoTide2 = ButterKnife.findById(infoItem, R.id.infoTide2);
            TextView infoTide3 = ButterKnife.findById(infoItem, R.id.infoTide3);
            TextView infoTide4 = ButterKnife.findById(infoItem, R.id.infoTide4);

            ImageView infoSun = ButterKnife.findById(infoItem, R.id.infoSun);
            ImageView infoMoon = ButterKnife.findById(infoItem, R.id.infoMoon);

            infoDate.setText(data.getDate());
            infoSunrise.setText(data.getSunrise());
            infoSunset.setText(data.getSunset());
            infoMoonrise.setText(data.getMoonrise());
            infoMoonset.setText(data.getMoonset());
            infoMajor1.setText(data.getMajor1());
            infoMajor2.setText(data.getMajor2());
            infoMinor1.setText(data.getMinor1());
            infoMinor2.setText(data.getMinor2());
            infoTideStation.setText(data.getTidestation());

            Log.d(TAG, "addBottomSheetContents: tide::: " + data.getTide());

            String[] infoTides = data.getTide().split("<br>");

            try {
                infoTide1.setText(infoTides[0].trim());
                infoTide3.setText(infoTides[1].trim());

                infoTide2.setText(infoTides[2].trim());
                infoTide4.setText(infoTides[3].trim());
            }catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }


            ((ViewGroup) itemContainer).addView(infoItem, i);

        }
    }

    // Loading progress bar in the bottom sheet view
    @Override
    public void setLoadingBottom(boolean isLoading) {
        if (isLoading) {

        } else {

        }
    }

    @Override
    public void flushBottomSheetContents() {
        ((ViewGroup) itemContainer).removeAllViews();
    }

    @Override
    public void setFabBottomVisibilty(boolean isVisible) {
        if (isVisible) {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fabSaveBtn.getLayoutParams();
            p.setAnchorId(R.id.design_bottom_sheet);
            p.anchorGravity = Gravity.END;
            fabSaveBtn.setLayoutParams(p);
            fabSaveBtn.show();
        } else {
            fabSaveBtn.setVisibility(View.INVISIBLE);
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fabSaveBtn.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fabSaveBtn.setLayoutParams(p);
        }
    }

    @Override
    public void showToast(final String message) {
        Toast.makeText(FishingMapActivity.this, message, Toast.LENGTH_SHORT).show();
        setLoadingBottom(false);
    }

    @Override
    public void removeMaker(Marker marker) {
        marker.remove();
        flushBottomSheetContents();
    }

    @Override
    protected void onStart() {
        if (!presenter.isMapConnected()) {
            presenter.connectMap();
        }
        presenter.connectService();
        super.onStart();
    }

    @Override
    protected void onResume() {
        presenter.startLocationUpdate();
        super.onResume();
    }

    @Override
    protected void onPause() {
        presenter.stopLocationUpdate();
        super.onPause();
    }


    @Override
    protected void onStop() {
        if (presenter.isMapConnected()) {
            presenter.disconnectMap();
        }
        presenter.disconnectService();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView(ViewInjector.nullObjectPatternView(this));
        unbinder.unbind();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // GoogleMap
        presenter.setGoogleMapConfiguration(googleMap);
        setGoogleMapListeners(googleMap);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick");
    }

    // fetch data, open the bottomsheet, and add a maker
    @Override
    public void onMapLongClick(LatLng latLng) {
        presenter.addUnsavedMarker(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        presenter.removeUnsavedMarker(marker, true);
        return false;
    }

    @Override
    public void onMapLoaded() {
        Log.d(TAG, "onMapLoaded");

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onCameraIdle() {
    }

    @Override
    public void onCameraMoveCanceled() {
    }

    @Override
    public void onCameraMove() {
    }

    @Override
    public void onCameraMoveStarted(int i) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case MapProvider.RESULT_CODE_LAST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission RESULT_CODE_LAST_LOCATION", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No Permission RESULT_CODE_LAST_LOCATION", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case MapProvider.RESULT_CODE_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission RESULT_CODE_REQUEST_LOCATION", Toast.LENGTH_SHORT).show();
                    presenter.startLocationUpdate();
                } else {
                    Toast.makeText(this, "No Permission RESULT_CODE_REQUEST_LOCATION", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case MapProvider.RESULT_CODE_LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission RESULT_CODE_LOCATION_PERMISSION", Toast.LENGTH_SHORT).show();
                    mapFragment.getMapAsync(this);
                } else {
                    Log.d(TAG, "Permission is not granted.");
                    Toast.makeText(this, "No Permission RESULT_CODE_LOCATION_PERMISSION", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MapProvider.REQUEST_CODE_LOCATION_UPDATE) {
            if (resultCode == RESULT_OK) {
                presenter.startLocationUpdate();
            } else {
                Toast.makeText(this, "The GPS is off", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * FAB action
     */
//    @Override
//    public void onCameraIdle() {
//        if (task != null && task.checkHide) {
//            task.execute();
//        }
//    }
//
//    @Override
//    public void onCameraMoveCanceled() {
//        if (task != null && task.getStatus() == AsyncTask.Status.PENDING) {
//            task.execute();
//        }
//    }
//
//    @Override
//    public void onCameraMove() {
//
//    }

//    @Override
//    public void onCameraMoveStarted(int i) {
//        if (task != null) {
//            switch (task.getStatus()) {
//                case PENDING:
//                    checkReset = false;
//                    break;
//                case FINISHED:
//                    checkReset = true;
//
//                    break;
//                case RUNNING:
//                    task.cancel(true);
//                    if (task.isCancelled()) {
//                        checkReset = true;
//                    }
//                    break;
//                default:
//                    break;
//            }
//
//        }
//
//        if (checkReset) {
//            _FABOnOffAsyncTask.resetTask();
//        }
//        task = _FABOnOffAsyncTask.getInstance(fabSaveBtn, 1000);
//        task.checkHide = true;
//    }


}
