package io.driden.fishtips;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void isTheTimeIncluded() {

        String currentDateStr = "16:30";
        String dateRange = "10:00 - 15:15";

        final String regex = "[0-9]{2}:[0-9]{2}";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(dateRange);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date cTime = null;
        try {
            cTime = sdf.parse(currentDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date[] timeArray = new Date[2];

        int count = 0;
        while (matcher.find()) {
            System.out.println("isTheTimeIncluded: " + matcher.group(0));
            try {
                timeArray[count++] = sdf.parse(matcher.group(0));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if ((timeArray[0].getTime() <= cTime.getTime()) &&
                (timeArray[1].getTime() >= cTime.getTime())) {
            System.out.println("isTheTimeIncluded: true");
        }
    }
}