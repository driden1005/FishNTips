package io.driden.fishtips.model;

import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class RealmFishingData {

    private double lat;
    private double lng;

    @Index
    @PrimaryKey
    private int index;

    private FishingData[] fishingDatas;

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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public FishingData[] getFishingDatas() {
        return fishingDatas;
    }

    public void setFishingDatas(FishingData[] fishingDatas) {
        this.fishingDatas = fishingDatas;
    }
}
