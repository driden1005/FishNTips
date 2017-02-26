package io.driden.fishtips.service;


import android.os.Bundle;

public interface ServiceInterface {

    interface ServiceCallback {
        void onSuccess(Bundle bundle);
        void onFailure(Bundle bundle);
    }

}
