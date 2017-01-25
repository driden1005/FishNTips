package io.driden.fishtips.common.Tasks;

/**
 * Created by driden on 23/01/2017.
 */

import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;


public class FABOnOffAsyncTask extends AsyncTask<Void, Void, Void> {

    private static FABOnOffAsyncTask mTaskInstance;
    private FloatingActionButton floatingActionButton;
    public int timerMilSec = 1000;
    public boolean checkHide = false;

    FABOnOffAsyncTask(FloatingActionButton floatingActionButton, int timerMilSec) {
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
        synchronized (FABOnOffAsyncTask.class) {
            mTaskInstance = null;
        }
    }

    public static FABOnOffAsyncTask getInstance(FloatingActionButton floatingActionButton, int milsec) {
        if (mTaskInstance == null) {
            synchronized (FABOnOffAsyncTask.class) {
                if (mTaskInstance == null) {

                    mTaskInstance = new FABOnOffAsyncTask(floatingActionButton, milsec);
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