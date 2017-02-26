package io.driden.fishtips.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Named;

import io.driden.fishtips.R;
import io.driden.fishtips.app.App;
import io.driden.fishtips.tasks.CookieRunner;
import io.driden.fishtips.tasks.MarkerInfoRunner;

public class NetworkService extends Service implements NetworkServiceInterface {

    private final String TAG = getClass().getSimpleName();

    final IBinder binder = new NetworkServiceBinder();

    @Inject
    @Named("network")
    SharedPreferences preferences;

    public class NetworkServiceBinder extends Binder {
        public NetworkService getService() {
            return NetworkService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        App.getAppComponent().inject(this);
        return binder;
    }

    @Override
    public void getCookie(ServiceCallback callback) {

        String cookieStr = preferences.getString(getString(R.string.key_cookie), "");

        ExecutorService executor;

        if ("".equals(cookieStr)) {
            executor = Executors.newSingleThreadExecutor();
            executor.execute(new CookieRunner(callback));
        } else {
            long cookieTime = preferences.getLong(getString(R.string.key_cookie_time), 0);
            long twelveHours = 1000 * 60 * 60 * 3;
            Log.d(TAG, "getCookie: :"+new Date().getTime());
            Log.d(TAG, "getCookie: :"+(cookieTime + twelveHours));
            if (new Date().getTime() < cookieTime + twelveHours) {
                if (callback != null) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("HAS_COOKIE", true);
                }
            } else {
                executor = Executors.newSingleThreadExecutor();
                executor.execute(new CookieRunner(callback));
            }
        }
    }

    @Override
    public void getFishingInfo(LatLng latLng, int days, Date date, TimeZone timeZone, ServiceCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new MarkerInfoRunner(latLng, days, date, timeZone, callback));
    }
}
