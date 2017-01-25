package io.driden.fishtips.Map;

import com.google.android.gms.location.LocationRequest;

import io.driden.fishtips.common.Enums.PriorityLevel;

/**
 * Created by driden on 15/01/2017.
 */

public class LocationPriority {

    public static int setPriority(PriorityLevel level){
        switch (level){
            case HIGH:
                return LocationRequest.PRIORITY_HIGH_ACCURACY;
            case MID:
                return LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
            case LOW:
                return LocationRequest.PRIORITY_LOW_POWER;
            case POOR:
                return LocationRequest.PRIORITY_NO_POWER;
            default:
                return LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        }
    }

}
