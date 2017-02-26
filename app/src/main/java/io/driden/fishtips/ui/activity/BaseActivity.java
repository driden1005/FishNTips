package io.driden.fishtips.ui.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {

    String viewTag;

    Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);

    }

    abstract void initView();

    abstract void getComponent();

    void bindView(){
        unbinder = ButterKnife.bind(this);
    }
}
