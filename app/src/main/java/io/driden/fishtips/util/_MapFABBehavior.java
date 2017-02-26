package io.driden.fishtips.util;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.MotionEvent;

import io.driden.fishtips.tasks._FABOnOffAsyncTask;

public class _MapFABBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    private _FABOnOffAsyncTask task;

    public _MapFABBehavior(Context context, AttributeSet attrs) {
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
                    _FABOnOffAsyncTask.resetTask();
                }
                task = _FABOnOffAsyncTask.getInstance(child, 1000);

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