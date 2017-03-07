package io.driden.fishtips.presenter;

import android.os.Handler;

import me.panavtec.threaddecoratedview.views.ThreadSpec;

public class MainThreadSpec implements ThreadSpec {
    Handler handler = new Handler();
    @Override public void execute(Runnable action) {
        handler.post(action);
    }
}