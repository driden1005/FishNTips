package io.driden.fishtips.util;


import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInteroceptor implements Interceptor{

    String cookieStr;

    public HeaderInteroceptor(String cookieStr){
        this.cookieStr = cookieStr;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original = chain.request();

        Request request = original.newBuilder()
                .header("User-Agent", "Android")
                .header("Cookie", cookieStr)
                .header("Referer", "https://www.bitetimes.com/")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Accept-Language", Locale.getDefault().getLanguage())
                .method(original.method(), original.body())
                .build();

        return chain.proceed(request);
    }
}
