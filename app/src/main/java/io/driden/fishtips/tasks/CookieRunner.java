package io.driden.fishtips.tasks;

import android.app.Application;
import android.os.Bundle;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.driden.fishtips.R;
import io.driden.fishtips.app.App;
import io.driden.fishtips.service.ServiceInterface;
import io.driden.fishtips.util.HttpProvider;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CookieRunner implements Runnable{

    private final String TAG = getClass().getSimpleName();

    @Inject
    @Named("default")
    OkHttpClient client;
    @Inject
    Application application;

    ServiceInterface.ServiceCallback callback;

    public CookieRunner(ServiceInterface.ServiceCallback callback){
        this.callback = callback;
        App.getAppComponent().inject(this);
    }

    @Override
    public void run() {
        Request request = new HttpProvider.RequestBuilder(application.getString(R.string.url_bite_times)).build();

        okhttp3.Response response = null;

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<HttpCookie> cookies = HttpCookie.parse(response.headers().get("Set-Cookie"));

        String cookieStr = "";

        for (HttpCookie cookie : cookies) {
            if ("PHPSESSID".equals(cookie.getName())) {
                cookieStr = cookie.toString();
                break;
            }
        }

        if(callback!=null){
            Bundle bundle = new Bundle();
            bundle.putString(application.getString(R.string.key_cookie), cookieStr);
            callback.onSuccess(bundle);
        }
    }
}
