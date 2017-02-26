package io.driden.fishtips.common;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.GoogleApiAvailability;

import static com.google.android.gms.common.ConnectionResult.SUCCESS;

public class CommonUtils {
    public static boolean checkPermissionGranted(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }

    public static void requestUsePermissions(Activity activity, String[] permissions, int resultCode) {
        ActivityCompat.requestPermissions(activity, permissions, resultCode);
    }

    public static boolean checkPlayService(Context context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        if (SUCCESS != resultCode) {
            return false;
        }
        return true;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
