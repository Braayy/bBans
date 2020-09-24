package braayy.bans;

import java.util.Calendar;

public class Util {

    public static String getFormattedEndDate(long end) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(end);

        return String.format(
                "%02d/%02d/%02d %02dH:%02dM",
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
        );
    }

}