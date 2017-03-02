package io.driden.fishtips.model;

import io.realm.RealmObject;

public class RealmFishingData extends RealmObject {

    private String tidestation;
    private String moonculmination;
    private String moonset;
    private String moonunderfoot;
    private String moonrise;
    private String sunset;
    private String sunrise;
    private String moonday;
    private String date;
    private String tide;
    private String minor1;
    private String minor1rating;
    private String min1color;
    private String minor2;
    private String minor2rating;
    private String min2color;
    private String major1;
    private String major1rating;
    private String maj1color;
    private String major2;
    private String major2rating;
    private String maj2color;
    private double lat;
    private double lng;

    public RealmFishingData() {

    }

    public RealmFishingData(FishingData data) {
        this.tidestation = data.getTidestation();
        this.moonculmination = data.getMoonculmination();
        this.moonset = data.getMoonset();
        this.moonunderfoot = data.getMoonunderfoot();
        this.moonrise = data.getMoonrise();
        this.sunset = data.getSunset();
        this.sunrise = data.getSunrise();
        this.moonday = data.getMoonday();
        this.tide = data.getTide();
        this.minor1 = data.getMinor1();
        this.minor1rating = data.getMinor1rating();
        this.min1color = data.getMin1color();
        this.minor2 = data.getMinor2();
        this.minor2rating = data.getMinor2rating();
        this.min2color = data.getMin2color();
        this.major1 = data.getMajor1();
        this.major1rating = data.getMajor1rating();
        this.maj1color = data.getMaj1color();
        this.major2 = data.getMajor2();
        this.major2rating = data.getMajor2rating();
        this.maj2color = data.getMaj2color();

        this.lat = data.getLat();
        this.lng = data.getLng();
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

    public String getMoonculmination() {
        return moonculmination;
    }

    public void setMoonculmination(String moonculmination) {
        this.moonculmination = moonculmination;
    }

    public String getTidestation() {
        return tidestation;
    }

    public void setTidestation(String tidestation) {
        this.tidestation = tidestation;
    }

    public String getMoonset() {
        return moonset;
    }

    public void setMoonset(String moonset) {
        this.moonset = moonset;
    }

    public String getMoonunderfoot() {
        return moonunderfoot;
    }

    public void setMoonunderfoot(String moonunderfoot) {
        this.moonunderfoot = moonunderfoot;
    }

    public String getMoonrise() {
        return moonrise;
    }

    public void setMoonrise(String moonrise) {
        this.moonrise = moonrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getMoonday() {
        return moonday;
    }

    public void setMoonday(String moonday) {
        this.moonday = moonday;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTide() {
        return tide;
    }

    public void setTide(String tide) {
        this.tide = tide;
    }

    public String getMinor1() {
        return minor1;
    }

    public void setMinor1(String minor1) {
        this.minor1 = minor1;
    }

    public String getMinor1rating() {
        return minor1rating;
    }

    public void setMinor1rating(String minor1rating) {
        this.minor1rating = minor1rating;
    }

    public String getMin1color() {
        return min1color;
    }

    public void setMin1color(String min1color) {
        this.min1color = min1color;
    }

    public String getMinor2() {
        return minor2;
    }

    public void setMinor2(String minor2) {
        this.minor2 = minor2;
    }

    public String getMinor2rating() {
        return minor2rating;
    }

    public void setMinor2rating(String minor2rating) {
        this.minor2rating = minor2rating;
    }

    public String getMin2color() {
        return min2color;
    }

    public void setMin2color(String min2color) {
        this.min2color = min2color;
    }

    public String getMajor1() {
        return major1;
    }

    public void setMajor1(String major1) {
        this.major1 = major1;
    }

    public String getMajor1rating() {
        return major1rating;
    }

    public void setMajor1rating(String major1rating) {
        this.major1rating = major1rating;
    }

    public String getMaj1color() {
        return maj1color;
    }

    public void setMaj1color(String maj1color) {
        this.maj1color = maj1color;
    }

    public String getMajor2() {
        return major2;
    }

    public void setMajor2(String major2) {
        this.major2 = major2;
    }

    public String getMajor2rating() {
        return major2rating;
    }

    public void setMajor2rating(String major2rating) {
        this.major2rating = major2rating;
    }

    public String getMaj2color() {
        return maj2color;
    }

    public void setMaj2color(String maj2color) {
        this.maj2color = maj2color;
    }

}
