package io.driden.fishtips.tasks;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Named;

import io.driden.fishtips.R;
import io.driden.fishtips.api.BiteTimesAPI;
import io.driden.fishtips.app.App;
import io.driden.fishtips.model.FishingData;
import io.driden.fishtips.model.FishingDataArrayParcelable;
import io.driden.fishtips.model.MarkersTag;
import io.driden.fishtips.provider.HttpProvider;
import io.driden.fishtips.service.ServiceInterface;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MarkerInfoRunner implements Runnable {

    private final String TAG = getClass().getSimpleName();
    @Inject
    Application application;

    @Inject
    @Named("XML")
    Retrofit retrofitXML;

    @Inject
    @Named("GSON")
    Retrofit retrofitGSON;

    @Inject
    @Named("network")
    SharedPreferences preferences;

    LatLng latLng;
    int days;
    Date date;
    TimeZone timeZone;

    ServiceInterface.ServiceCallback callback;

    public MarkerInfoRunner(LatLng latLng, int days, Date date, TimeZone timeZone, ServiceInterface.ServiceCallback callback) {
        this.latLng = latLng;
        this.days = days;
        this.date = date;
        this.timeZone = timeZone;
        this.callback = callback;

        App.getAppComponent().inject(this);
    }

    public String getName() {
        return Thread.currentThread().getName();
    }

    @Override
    public void run() {

        try {
            final String cookieStr = preferences.getString(application.getString(R.string.key_cookie), "");
            Log.d(TAG, "run: " + cookieStr);
            OkHttpClient cl = new HttpProvider.ClientBuilder()
                    .setDefaultTimeOuts()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
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
                    })
                    .build();

            Retrofit retrofitXML = new Retrofit.Builder().baseUrl(application.getString(R.string.url_bite_times))
                    .client(cl).addConverterFactory(SimpleXmlConverterFactory.create())
                    .build();
            BiteTimesAPI api = retrofitXML.create(BiteTimesAPI.class);

            Call<MarkersTag> tideCall = api.getTideStation(latLng.latitude, latLng.longitude, 2000);

            Response<MarkersTag> tideResponse = tideCall.execute();

            MarkersTag markersTag = tideResponse.body();

            api = retrofitGSON.create(BiteTimesAPI.class);

            String tideStation = markersTag.getList().get(0).getName() == null ? "" : markersTag.getList().get(0).getName();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

            Call<ResponseBody> dataCall = api.getData(
                    latLng.latitude,
                    latLng.longitude,
                    days,
                    tideStation,
                    timeZone.getID(),
                    sdf.format(date)
            );

            Response<ResponseBody> dataResponse = dataCall.execute();
            String result = dataResponse.body().string();

//            Log.d(TAG, "run: "+result);

            Gson gson = new Gson();
            Type dataType = new TypeToken<HashMap<String, FishingData>>() {
            }.getType();
            HashMap<String, FishingData> datas = gson.fromJson(result, dataType);

            FishingData[] dataArray = new FishingData[days];

            for (int i = 0; i < days; i++) {
                dataArray[i] = datas.get(Integer.toString(i));
                dataArray[i].setLat(latLng.latitude);
                dataArray[i].setLng(latLng.longitude);
            }

            FishingDataArrayParcelable dataParcel = new FishingDataArrayParcelable();
            dataParcel.setDataArray(dataArray);

            Bundle bundle = new Bundle();
            bundle.putParcelable("FISHING_DATA", dataParcel);

            callback.onSuccess(bundle);
        } catch (Exception e) {
            e.printStackTrace();
            Bundle bundle = new Bundle();
            bundle.putString("MESSAGE", "Marker Fetch Error:" + e.getMessage());
            callback.onFailure(bundle);
        }
    }

}
