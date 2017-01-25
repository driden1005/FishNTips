package io.driden.fishtips;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import io.driden.fishtips.Map.MapActivity;
import io.driden.fishtips.Map.ScrollingActivity;
import io.driden.fishtips.Map.TestMapActivity;

public class SplashActivity extends AppCompatActivity {

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            Intent intent = new Intent(SplashActivity.this, MapActivity.class);
            Intent intent = new Intent(SplashActivity.this, TestMapActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            handler.sendEmptyMessage(0);
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        handler.postDelayed(runnable, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
