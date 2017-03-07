package io.driden.fishtips.dependancy.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.driden.fishtips.api.BiteTimesAPI;
import io.driden.fishtips.model.MarkersTag;
import io.driden.fishtips.model.TideStation;
import io.driden.fishtips.provider.HttpProvider;
import io.driden.fishtips.util.TideStationXMLParser;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by driden on 3/02/2017.
 */
public class NetModuleTest {


    private final String TAG = getClass().getSimpleName();
    Retrofit retrofit;
    String baseUrl = "https://www.bitetimes.com";
    OkHttpClient client;

    public NetModuleTest() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .client(client)
//                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    @Test
    public void provideRetrofit() throws Exception {

        // Get Cookie
        Request request = new Request.Builder().url(baseUrl).build();
        okhttp3.Response response = client.newCall(request).execute();

        List<HttpCookie> cookies = HttpCookie.parse(response.headers().get("Set-Cookie"));

        String cookieStr = "";

        for (HttpCookie cookie : cookies) {
            if ("PHPSESSID".equals(cookie.getName())) {
                cookieStr = cookie.toString();
            }
        }

        System.out.println(cookieStr);

        // https://futurestud.io/tutorials/retrofit-add-custom-request-header
        // http://gun0912.tistory.com/50
        //ToDo 쿠키 만료일 검증해서 갱신하는 로직 생성하기

        final String finalCookieStr = cookieStr;




        // OKhttp3
        Request request3 = new Request.Builder()
                .header("User-Agent", "Android")
                .header("Cookie", finalCookieStr)
                .header("Referer", "https://www.bitetimes.com/")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .url("https://www.bitetimes.com/cgi-bin/worlddata.cgi?latdeg=-37.973502&lngdeg=174.848778&numdays=2&tidestation=Aotea+Harbour%2C+New+Zealand&date_time=04-Feb-2017&tzone=Pacific%2FAuckland")
                .build();

        okhttp3.Response response3 = client.newCall(request3).execute();







        // Tide Station
        // /phpsqlsearch_genxml.php?lat=-37.73495246197548&lng=174.86525763037116&radius=2000

        RequestBody requestBody5 = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("lat", "-37.973502")
                .addFormDataPart("lng", "174.848778")
                .addFormDataPart("radius", "2000")
                .build();

        Request request5 = new Request.Builder()
                .url(baseUrl + "/phpsqlsearch_genxml.php?lat=-37.73495246197548&lng=174.86525763037116&radius=2000")
                .get()
                .header("User-Agent", "Android")
                .header("Cookie", finalCookieStr)
                .header("Referer", "https://www.bitetimes.com/")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .build();

        okhttp3.Response response5 = client.newCall(request5).execute();

        String strBody = response5.body().string();

        List<TideStation> list = TideStationXMLParser.parseTideStation(strBody);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("latdeg", "-37.973502")
                .addFormDataPart("lngdeg", "174.848778")
                .addFormDataPart("numdays", "30")
                .addFormDataPart("tidestation", list.get(0).getName())
                .addFormDataPart("tzone", "Pacific/Auckland")
                .addFormDataPart("date_time", "04-Feb-2017")
                .build();

        Request request4 = new Request.Builder()
                .url(baseUrl + "/cgi-bin/worlddata.cgi")
                .method("POST", RequestBody.create(null, new byte[0]))
                .post(requestBody)
                .header("User-Agent", "Android")
                .header("Cookie", finalCookieStr)
                .header("Referer", "https://www.bitetimes.com/")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .build();

        okhttp3.Response response4 = client.newCall(request4).execute();

        System.out.println("---------------------------------------------------------------");

        System.out.println(response4.body().string());



    }

    @Test
    public void newTest() throws IOException {

        // Get Cookie
        Request request = new Request.Builder().url(baseUrl).build();
        okhttp3.Response response = client.newCall(request).execute();

        List<HttpCookie> cookies = HttpCookie.parse(response.headers().get("Set-Cookie"));

        String cookieStr = "";

        for (HttpCookie cookie : cookies) {
            if ("PHPSESSID".equals(cookie.getName())) {
                cookieStr = cookie.toString();
            }
        }

        System.out.println(cookieStr);

        // https://futurestud.io/tutorials/retrofit-add-custom-request-header
        // http://gun0912.tistory.com/50
        //ToDo 쿠키 만료일 검증해서 갱신하는 로직 생성하기

        final String finalCookieStr = cookieStr;


        /**
         * Retrofit2 new version
         */
        OkHttpClient clientNew = new HttpProvider
                .ClientBuilder()
                .setDefaultTimeOuts()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {

                        Request original = chain.request();

                        Request request = original.newBuilder()
                                .header("User-Agent", "Android")
                                .header("Cookie", finalCookieStr)
                                .header("Referer", "https://www.bitetimes.com/")
                                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                .header("Accept-Language", Locale.getDefault().getLanguage())
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    }
                })
                .build();

        Retrofit retrofitNew = new Retrofit.Builder().baseUrl(baseUrl).client(clientNew).build();

        TestAPI testAPI = retrofitNew.create(TestAPI.class);

        Call<ResponseBody> callTide = testAPI.getTideStation(
                -39.326664,
                175.020783,
                2000
        );

        Call<ResponseBody> callData = testAPI.getData(
                -39.326664,
                175.020783,
                5,
                "Raglan, New Zealand",
                "Pacific/Auckland",
                "04-Mar-2017"
        );


        Response<ResponseBody> resultData = callData.execute();
        Response<ResponseBody> resultTideStation = callTide.execute();

        System.out.println(resultTideStation.body().string());
        System.out.println(resultData.body().string());

        Retrofit re = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(clientNew)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        BiteTimesAPI api = re.create(BiteTimesAPI.class);

        Call<MarkersTag> tideCall = api.getTideStation(-39.326664, 175.020783, 2000);

        Response<MarkersTag> tideResponse = null;
        try {
            tideResponse = tideCall.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MarkersTag markersTag = tideResponse.body();

        System.out.println("markers:"+markersTag.getList().size());
    }


}
