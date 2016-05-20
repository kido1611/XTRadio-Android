package id.xt.radio.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kido1611 on 19-May-16.
 */
public class FacebookUtils {

    public static String getDate(String time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        try {
            Date mDate = format.parse(time);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, HH:mm");

            return sdf.format(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
}
