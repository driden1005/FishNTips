package io.driden.fishtips.dependancy.module;

import android.app.Application;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.driden.fishtips.R;
import io.driden.fishtips.util.HeaderInteroceptor;
import io.driden.fishtips.util.HttpProvider;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@Module
public class NetModule {

    private String baseUrl;

    public NetModule(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder().create();
    }

    @Provides
    @Singleton
    Cache provideHttpCache(Application application) {
        int cacheSize = 10 * 1024 * 1024;
        return new Cache(application.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    @Named("default")
    OkHttpClient provideHttpClient(Cache cache) {
        return new HttpProvider.ClientBuilder()
                .setDefaultTimeOuts()
                .setCache(cache)
                .build();
    }

    @Provides
    @Singleton
    @Named("header")
    OkHttpClient provideHttpClientH(Cache cache, @Named("network") SharedPreferences preferences, Application application) {

        final String cookieStr = preferences.getString(application.getString(R.string.key_cookie), "");

        return new HttpProvider.ClientBuilder()
                .setDefaultTimeOuts()
                .setCache(cache)
                .addInterceptor(new HeaderInteroceptor(cookieStr))
                .build();
    }

    @Provides
    @Singleton
    @Named("GSON")
    Retrofit provideRetrofitGSON(@Named("header") OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    @Named("XML")
    Retrofit provideRetrofitXML(@Named("header") OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
    }

}
