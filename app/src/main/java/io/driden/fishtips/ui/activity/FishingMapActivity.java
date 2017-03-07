package io.driden.fishtips.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import javax.inject.Inject;

import butterknife.BindView;
import io.driden.fishtips.R;
import io.driden.fishtips.app.App;
import io.driden.fishtips.model.FishingData;
import io.driden.fishtips.presenter.FishingMapPresenter;
import io.driden.fishtips.presenter.MainThreadSpec;
import io.driden.fishtips.provider.MapProviderImpl;
import io.driden.fishtips.service.NetworkService;
import io.driden.fishtips.view.BottomItemView;
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

    /**
     * Adding child views of the bottom sheet view.
     *
     * @param latLng
     * @param dataArray
     */
    @Override
    public void addBottomSheetContents(final LatLng latLng, final FishingData[] dataArray) {

        ((ViewGroup) itemContainer).removeAllViews();

        for (int i = 0; i < dataArray.length; i++) {

            FishingData data = dataArray[i];

            BottomItemView bottomItemView = new BottomItemView(getApplicationContext());
            bottomItemView.setData(data);

            ((ViewGroup) itemContainer).addView(bottomItemView, i);

        }

        Animation fabForward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_forward);

        fabSaveBtn.startAnimation(fabForward);
        // Save the current marker.
        fabSaveBtn.setOnClickListener(v -> presenter.saveMarker(latLng, dataArray));
    }

    /**
     * Loading progress bar in the bottom sheet view
     */
    @Override
    public void setLoadingBottom(boolean isLoading) {
        if (isLoading) {
            // todo visible loading
        } else {
            // todo invisible loading
        }
    }

    /**
     * flush child views of the bottom sheet view.
     */
    @Override
    public void flushBottomSheetContents() {
        ((ViewGroup) itemContainer).removeAllViews();
    }

    /**
     * set visibility of the floating action button
     *
     * @param isVisible
     */
    @Override
    public void setFabBottomVisibilty(boolean isVisible) {

        if (isVisible) {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fabSaveBtn.getLayoutParams();
            p.setAnchorId(R.id.design_bottom_sheet);
            p.anchorGravity = Gravity.END;
            fabSaveBtn.setLayoutParams(p);
            fabSaveBtn.show();
        } else {
            fabSaveBtn.clearAnimation();
            fabSaveBtn.setVisibility(View.INVISIBLE);
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fabSaveBtn.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fabSaveBtn.setLayoutParams(p);
        }
    }

    @Override
    public void showToast(final String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        setLoadingBottom(false);
    }

    public void showSnackBar(String message) {
        Snackbar.make(bottomSheet, message, Snackbar.LENGTH_LONG).show();
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
        presenter.setSavedMarkersVisibility(true);
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
        if (marker.getTag() == null) {
            presenter.removeUnsavedMarker(marker, true);
        } else {
            presenter.getSavedFishingData(marker);
        }
        return false;
    }

    @Override
    public void onMapLoaded() {
        presenter.getSavedMarkers();
        presenter.setSavedMarkersVisibility(true);

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
            case MapProviderImpl.RESULT_CODE_LAST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission RESULT_CODE_LAST_LOCATION", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No Permission RESULT_CODE_LAST_LOCATION", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case MapProviderImpl.RESULT_CODE_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission RESULT_CODE_REQUEST_LOCATION", Toast.LENGTH_SHORT).show();
                    presenter.startLocationUpdate();
                } else {
                    Toast.makeText(this, "No Permission RESULT_CODE_REQUEST_LOCATION", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case MapProviderImpl.RESULT_CODE_LOCATION_PERMISSION: {
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
        if (requestCode == MapProviderImpl.REQUEST_CODE_LOCATION_UPDATE) {
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
