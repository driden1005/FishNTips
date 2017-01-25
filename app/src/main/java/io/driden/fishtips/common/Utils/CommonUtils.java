package io.driden.fishtips.common.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by driden on 17/01/2017.
 */

public class CommonUtils {
    public static boolean checkPermission(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }
    public static void requestUsePermissions(Activity activity, String[]permissions, int resultCode) {
        ActivityCompat.requestPermissions(activity, permissions, resultCode);
    }
}
