package io.driden.fishtips.provider;

import android.util.Log;

import com.google.android.gms.maps.model.Marker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.driden.fishtips.model.RealmFishingData;
import io.driden.fishtips.model.RealmLatLng;
import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.RealmResults;

public class MarkerProvider {

    private final String TAG = getClass().getSimpleName();
    Realm realm;

    public MarkerProvider(Realm realm){
        this.realm = realm;
    }

    public ArrayList<Marker> getSavedMarkers(MapProvider mapProvider) {
        RealmResults<RealmLatLng> savedLatLngs = realm.where(RealmLatLng.class).findAll();

        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM");
        String currentDateStr = sdf.format(currentDate);

        ArrayList<Marker> markers = new ArrayList<>();

        Observable.range(0, savedLatLngs.size())
                .subscribe(i -> {
                    RealmFishingData data =
                            realm.where(RealmFishingData.class)
                                    .equalTo("date", currentDateStr)
                                    .equalTo("milisec", savedLatLngs.get(i).getMiliSec())
                                    .findFirst();
                    Marker marker = getColoredMarker(mapProvider, currentDate, savedLatLngs.get(i), data);
                    marker.setTag(i);
                    markers.add(marker);
                }, e -> {
                    Log.d(TAG, "getSavedMarkers: " + e.getMessage());
                    if (!markers.isEmpty()) {
                        setMarkersVisibility(mapProvider, markers, true);
                    }
                }, () -> setMarkersVisibility(mapProvider,markers, true));

        return markers;
    }

    /**
     * Just check Icon Colors for the current time.
     *
     * @param marker
     * @param currentDate
     * @param data
     * @return
     */
    public Marker getMarkerIconColors(MapProvider mapProvider, Marker marker, Date currentDate, RealmFishingData data) {
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

    public Marker getColoredMarker(MapProvider mapProvider, Date currentDate, RealmLatLng realmlatLng, RealmFishingData data) {

        Marker marker;

        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM");
        String currentDateStr = sdf.format(currentDate);

        if (data != null && currentDateStr.equals(data.getDate())) {
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
     * set the saved markers' visibility
     *
     * @param isVisible
     */
    private void setMarkersVisibility(MapProvider mapProvider, List<Marker> markers, boolean isVisible) {
        mapProvider.setMarkersVisible(true, markers);
    }

}
