package io.driden.fishtips.Map;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.MotionEvent;

import io.driden.fishtips.common.Tasks.FABOnOffAsyncTask;

/**
 * Created by driden on 23/01/2017.
 */

public class MapFABBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    private FABOnOffAsyncTask task;

    public MapFABBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, FloatingActionButton child, MotionEvent ev) {

        boolean checkReset = false;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (task != null) {
                    switch (task.getStatus()) {
                        case PENDING:
                            checkReset = false;
                            break;
                        case FINISHED:
                            checkReset = true;

                            break;
                        case RUNNING:
                            task.cancel(true);
                            if (task.isCancelled()) {
                                checkReset = true;
                            }
                            break;
                        default:
                            break;
                    }

                }

                if (checkReset) {
                    FABOnOffAsyncTask.resetTask();
                }
                task = FABOnOffAsyncTask.getInstance(child, 1000);

                return false;

            case MotionEvent.ACTION_UP:
                task.execute();
                return false;
            case MotionEvent.ACTION_MOVE:
                return false;
            default:
                return super.onInterceptTouchEvent(parent, child, ev);
        }
    }
}