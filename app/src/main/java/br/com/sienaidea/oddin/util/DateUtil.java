package br.com.sienaidea.oddin.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Siena Idea on 03/05/2016.
 */
public class DateUtil {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
    String dateString = "2015-06-18T05:05:00.880483Z";

    /*
        try {
            Date date = df.parse(dateString);
            df.applyPattern("dd/mm/yyyy hh:mm:ss");
            String result = df.format(date);
            Log.d("DATA", result);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        */

    /*
    public static String getDateFormarDDMMYYYY(){
        DateFormat inputFormat = new DateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new DateFormat("dd MMM yyyy");
        String inputDateStr="2013-06-24";
        //Date date = inputFormat.parse(inputDateStr);
        //String outputDateStr = outputFormat.format(date);

        //return outputDateStr;
    }
    */


    public static String getDateFormat(String datestr) {
        Date date = String2Date(datestr);
        return getDateString(date);
    }

    public static String getTimeFormat(String datestr) {
        Date date = String2Date(datestr);
        return getTimeString(date);
    }



    public static String getDateTimeString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static String getDateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    public static String getTimeString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);
    }

    public static Date String2Date(String datestr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(datestr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
