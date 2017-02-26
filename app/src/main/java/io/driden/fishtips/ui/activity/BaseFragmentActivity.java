package io.driden.fishtips.ui.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;

import butterknife.ButterKnife;

public abstract class BaseFragmentActivity extends FragmentActivity {

    String viewTag;
    String TAG;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);
    }

    abstract void initView();

    void bindView(){
        ButterKnife.bind(this);
    }

    abstract String getTag();
}
