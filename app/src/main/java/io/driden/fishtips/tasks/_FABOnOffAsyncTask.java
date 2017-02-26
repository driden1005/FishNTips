package io.driden.fishtips.tasks;

import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;


public class _FABOnOffAsyncTask extends AsyncTask<Void, Void, Void> {

    private static _FABOnOffAsyncTask mTaskInstance;
    private FloatingActionButton floatingActionButton;
    int timerMilSec = 1000;
    public boolean checkHide = false;

    _FABOnOffAsyncTask(FloatingActionButton floatingActionButton, int timerMilSec) {
        if (timerMilSec < 0) {
            this.timerMilSec = 1000;
        } else {
            this.timerMilSec = timerMilSec;
        }
        this.floatingActionButton = floatingActionButton;
        this.floatingActionButton.setEnabled(false);
        this.floatingActionButton.hide();
    }

    public static void resetTask() {
        synchronized (_FABOnOffAsyncTask.class) {
            mTaskInstance = null;
        }
    }

    public static _FABOnOffAsyncTask getInstance(FloatingActionButton floatingActionButton, int milsec) {
        if (mTaskInstance == null) {
            synchronized (_FABOnOffAsyncTask.class) {
                if (mTaskInstance == null) {

                    mTaskInstance = new _FABOnOffAsyncTask(floatingActionButton, milsec);
                }
            }
        }
        return mTaskInstance;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Thread.sleep(timerMilSec);
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        floatingActionButton.show();
        floatingActionButton.setEnabled(true);
    }
}