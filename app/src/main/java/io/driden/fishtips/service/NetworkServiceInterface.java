package io.driden.fishtips.service;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;

public interface NetworkServiceInterface extends ServiceInterface{
    void getCookie(ServiceCallback callback);

    ExecutorService getFishingInfo(LatLng latLng, int days, Date date, TimeZone timeZone, ServiceCallback callback);
}
