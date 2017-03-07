package io.driden.fishtips.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.ButterKnife;
import io.driden.fishtips.R;
import io.driden.fishtips.app.App;
import io.driden.fishtips.model.FishingData;
import io.driden.fishtips.view.moonphase.MoonView;

public class BottomItemView extends LinearLayout {
    private final String TAG = getClass().getSimpleName();
    @Inject
    DisplayMetrics metrics;


    public BottomItemView(Context context) {
        super(context);
    }

    public BottomItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BottomItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setData(FishingData data) {

        App.getAppComponent().inject(this);

        View view = inflate(getContext(), R.layout.bite_info_item, null);
        TextView infoDate = ButterKnife.findById(view, R.id.infoDate);
        TextView infoSunrise = ButterKnife.findById(view, R.id.infoSunrise);
        TextView infoSunset = ButterKnife.findById(view, R.id.infoSunset);
        TextView infoMoonrise = ButterKnife.findById(view, R.id.infoMoonrise);
        TextView infoMoonset = ButterKnife.findById(view, R.id.infoMoonset);
        TextView infoMajor1 = ButterKnife.findById(view, R.id.infoMajor1);
        TextView infoMajor2 = ButterKnife.findById(view, R.id.infoMajor2);
        TextView infoMinor1 = ButterKnife.findById(view, R.id.infoMinor1);
        TextView infoMinor2 = ButterKnife.findById(view, R.id.infoMinor2);
        TextView infoTideStation = ButterKnife.findById(view, R.id.infoTideStation);

        ImageView infoSun = ButterKnife.findById(view, R.id.infoSun);
        MoonView infoMoon = ButterKnife.findById(view, R.id.infoMoon);

        infoDate.setText(data.getDate());
        infoSunrise.setText(data.getSunrise());
        infoSunset.setText(data.getSunset());
        infoMoonrise.setText(data.getMoonrise());
        infoMoonset.setText(data.getMoonset());

        infoMajor1.setText(getTime(data.getMajor1()));
        getTextColor(data.getMaj1color(), infoMajor1);

        infoMajor2.setText(getTime(data.getMajor2()));
        getTextColor(data.getMaj2color(), infoMajor2);

        infoMinor1.setText(getTime(data.getMinor1()));
        getTextColor(data.getMin1color(), infoMinor1);

        infoMinor2.setText(getTime(data.getMinor2()));
        getTextColor(data.getMin2color(), infoMinor2);

        infoTideStation.setText(data.getTidestation());

        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM");
        Date targetDate;
        try {
            targetDate = sdf.parse(data.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
            targetDate = new Date();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH);    // 0 ~ 11

        cal.setTime(targetDate);
        int targetMonth = cal.get(Calendar.MONTH);

        if (currentMonth == 11 && targetMonth == 0) {
            cal.set(Calendar.YEAR, (currentYear + 1));
        } else {
            cal.set(Calendar.YEAR, currentYear);
        }

        targetDate = cal.getTime();

        infoMoon.loadMoonImage(targetDate);

        GridLayout tideTimes = ButterKnife.findById(view, R.id.tideTimes);

        String[] infoTides = data.getTide().split("<br>");

        for (String infoTide : infoTides) {
            View tideItem = LayoutInflater.from(getContext()).inflate(R.layout.tide_item, null);
            ImageView ivIcon = ButterKnife.findById(tideItem, R.id.tideIcon);
            ivIcon.setImageResource(R.drawable.lowtide);
            TextView tvTime = ButterKnife.findById(tideItem, R.id.tideTime);
            tvTime.setText(infoTide);
            tideItem.setMinimumWidth(metrics.widthPixels / 2);
            tideTimes.addView(tideItem);
        }

        view.setMinimumWidth(metrics.widthPixels);

        addView(view);
    }

    String getTime(String str) {
        if (!"".equals(str.trim())) {
            return str.trim();
        } else {
            return "--:--";
        }
    }

    void getTextColor(String str, TextView tv) {

        @ColorInt int colorInt;

        switch (str.trim()) {
            case "text-red":
                colorInt = ContextCompat.getColor(getContext(), R.color.bite_red);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tv.setTextAppearance(R.style.textBold);
                } else {
                    tv.setTextAppearance(getContext(), R.style.textBold);
                }
                break;
            case "text-orange":
                colorInt = ContextCompat.getColor(getContext(), R.color.bite_orange);
                break;
            case "text-green":
                colorInt = ContextCompat.getColor(getContext(), R.color.bite_green);
                break;
            case "text-cyan":
                colorInt = ContextCompat.getColor(getContext(), R.color.bite_cyan);
                break;
            case "text-black":
                colorInt = ContextCompat.getColor(getContext(), R.color.bite_black);
                break;
            default:
                colorInt = ContextCompat.getColor(getContext(), R.color.bite_black);
        }
        tv.setTextColor(colorInt);
    }
}
