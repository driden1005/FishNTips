package io.driden.fishtips.dependancy.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.driden.fishtips.R;
import io.realm.Realm;
import io.realm.RealmConfiguration;

@Module
public class DataModule {

    public DataModule(Application application) {
        Realm.init(application);
    }

    @Provides
    @Singleton
    Realm provideRealm() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        return Realm.getInstance(config);
    }

    @Provides
    @Singleton
    @Named("network")
    SharedPreferences providePreferencesNetwork(Application application){
        return application.getSharedPreferences(application.getString(R.string.key_preference_NETWORK), Context.MODE_PRIVATE);
    }
}
