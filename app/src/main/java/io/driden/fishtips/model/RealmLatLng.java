package io.driden.fishtips.model;

import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmLatLng extends RealmObject {

    private double lat;
    private double lng;

    public RealmLatLng() {

    }

    public RealmLatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public RealmLatLng(RealmList<RealmFishingData> datas) {
        this.lat = datas.get(0).getLat();
        this.lng = datas.get(0).getLng();
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
