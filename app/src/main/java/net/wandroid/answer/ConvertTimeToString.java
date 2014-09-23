package net.wandroid.answer;

import java.text.SimpleDateFormat;

public class ConvertTimeToString {

    public String fromMillisecondsToDateString(long ms,String dateFormat) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        String formattedDate = df.format(ms);
        return formattedDate;
    }

}
