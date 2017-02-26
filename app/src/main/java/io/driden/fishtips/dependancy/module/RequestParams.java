package io.driden.fishtips.dependancy.module;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.TimeZone;

public class RequestParams {

    private LatLng latLng;
    private int days;
    private Date date;
    private TimeZone timeZone;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}
