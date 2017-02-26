package io.driden.fishtips.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FishingData implements Parcelable {

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


    protected FishingData(Parcel in) {
        tidestation = in.readString();
        moonculmination = in.readString();
        moonset = in.readString();
        moonunderfoot = in.readString();
        moonrise = in.readString();
        sunset = in.readString();
        sunrise = in.readString();
        moonday = in.readString();
        date = in.readString();
        tide = in.readString();
        minor1 = in.readString();
        minor1rating = in.readString();
        min1color = in.readString();
        minor2 = in.readString();
        minor2rating = in.readString();
        min2color = in.readString();
        major1 = in.readString();
        major1rating = in.readString();
        maj1color = in.readString();
        major2 = in.readString();
        major2rating = in.readString();
        maj2color = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public static final Creator<FishingData> CREATOR = new Creator<FishingData>() {
        @Override
        public FishingData createFromParcel(Parcel in) {
            return new FishingData(in);
        }

        @Override
        public FishingData[] newArray(int size) {
            return new FishingData[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tidestation);
        dest.writeString(moonculmination);
        dest.writeString(moonset);
        dest.writeString(moonunderfoot);
        dest.writeString(moonrise);
        dest.writeString(sunset);
        dest.writeString(sunrise);
        dest.writeString(moonday);
        dest.writeString(date);
        dest.writeString(tide);
        dest.writeString(minor1);
        dest.writeString(minor1rating);
        dest.writeString(min1color);
        dest.writeString(minor2);
        dest.writeString(minor2rating);
        dest.writeString(min2color);
        dest.writeString(major1);
        dest.writeString(major1rating);
        dest.writeString(maj1color);
        dest.writeString(major2);
        dest.writeString(major2rating);
        dest.writeString(maj2color);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }
}
