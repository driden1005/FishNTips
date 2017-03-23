package io.driden.fishtips.tasks;

import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import io.driden.fishtips.model.FishingDataArrayParcelable;
import io.driden.fishtips.model.RealmFishingData;
import io.driden.fishtips.model.RealmLatLng;
import io.driden.fishtips.service.ServiceInterface;
import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class FishingDataUpdater {

    Realm realm;
    @Inject
    FishingDataUpdater(Realm realm) {
        this.realm = realm;
    }

    /**
     * update fishing data. It will instantiated at the background service
     */
    public void updateFishingData() {
        // get lats lngs
        RealmResults<RealmLatLng> savedLatLngs = realm.where(RealmLatLng.class).findAll();

        ExecutorService executor = Executors.newFixedThreadPool(savedLatLngs.size());

        Observable<RealmLatLng> observable = Observable.fromIterable(savedLatLngs);

        // network connections and get data
        observable.subscribe(realmLatLng -> {
            executor.execute(new MarkerInfoRunner(new LatLng(realmLatLng.getLat(), realmLatLng.getLng()), 2,
                    new Date(), TimeZone.getDefault(), new ServiceInterface.ServiceCallback() {
                @Override
                public void onSuccess(Bundle bundle) {
                    FishingDataArrayParcelable parcel = bundle.getParcelable("FISHING_DATA");

                    RealmList<RealmFishingData> dataList = new RealmList<>();

                    // Convert the array to the Realm collection.
                    Arrays.asList(parcel.getDataArray()).stream()
                            .map(fishingData -> new RealmFishingData(fishingData))
                            .peek(realmFishingData -> realmFishingData.setMiliSec(realmLatLng.getMiliSec()))
                            .forEach(realmFishingData -> dataList.add(realmFishingData));

                    realm.beginTransaction();
//                    realm.insertOrUpdate(realmLatLng);
                    realm.insertOrUpdate(dataList);
                    realm.commitTransaction();
                }

                @Override
                public void onFailure(Bundle bundle) {

                }
            }));
        });
    }

}
