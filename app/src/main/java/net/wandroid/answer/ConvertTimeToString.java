package net.wandroid.answer;

import java.text.SimpleDateFormat;

/**
 * Utility class that handles conversion of time to string
 */
public class ConvertTimeToString {

    /**
     * Converts milliseconds to a formatted string
     * @param ms time in ms
     * @param dateFormat date format to be used
     * @return the formatted string
     */
    public String fromMillisecondsToDateString(long ms,String dateFormat) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        String formattedDate = df.format(ms);
        return formattedDate;
    }

}
