package io.driden.fishtips.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import io.driden.fishtips.R;

public class MarkerIconFactory {

    public static Bitmap getIcon(Context context, String colorName) {
        switch (colorName) {
            // Excellent
            case "text-red":
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_icon_red);
            // Good
            case "text-orange":
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_icon_orange);
            // Average
            case "text-green":
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_icon_green);
            // Fair
            case "text-cyan":
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_icon_cyan);
            // Bad
            case "text-black":
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_icon_black);
            default:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_icon_default);
        }
    }

    public static Bitmap getIcon(Context context, IconColor color) {
        switch (color) {
            // Excellent
            case RED:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_icon_red);
            // Good
            case ORANGE:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_icon_orange);
            // Average
            case GREEN:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_icon_green);
            // Fair
            case CYAN:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_icon_cyan);
            // Bad
            case BLACK:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_icon_black);
            default:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.test_marker);
        }
    }

    public enum IconColor {
        RED,
        ORANGE,
        GREEN,
        CYAN,
        BLACK
    }
}
