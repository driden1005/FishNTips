package io.driden.fishtips.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class RealmLatLng extends RealmObject {

    private double lat;
    private double lng;
    @Index
    @PrimaryKey
    private long miliSec;

    public RealmLatLng() {

    }

    public RealmLatLng(FishingData[] datas) {
        this.lat = datas[0].getLat();
        this.lng = datas[0].getLng();
        this.miliSec = new Date().getTime();
    }

    public long getMiliSec() {
        return miliSec;
    }

    public void setMiliSec(long miliSec) {
        this.miliSec = miliSec;
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
