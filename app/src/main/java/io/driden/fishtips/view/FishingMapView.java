package io.driden.fishtips.view;

import com.google.android.gms.maps.model.LatLng;

import io.driden.fishtips.model.FishingData;
import me.panavtec.threaddecoratedview.views.qualifiers.ThreadDecoratedView;

@ThreadDecoratedView
public interface FishingMapView extends BaseView {

    void addBottomSheetContents(final LatLng latLng, final FishingData[] dataArra, boolean isNew);

    void setLoadingBottom(boolean isLoading);

    void flushBottomSheetContents();

    void setFabBottomVisibilty(boolean isVisible);

    void showToast(String message);
}