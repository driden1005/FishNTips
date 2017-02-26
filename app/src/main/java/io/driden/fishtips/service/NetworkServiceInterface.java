package io.driden.fishtips.service;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.TimeZone;

public interface NetworkServiceInterface extends ServiceInterface{
    void getCookie(ServiceCallback callback);
    void getFishingInfo(LatLng latLng, int days, Date date, TimeZone timeZone, ServiceCallback callback);
}
