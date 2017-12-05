package jp.ne.jinoxst.mat.itg.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static String getDateTime(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getSaleDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(cal.getTime());
    }

    public static String getSaleDateFirstDay() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        return sdf.format(cal.getTime()) + "01";
    }

    public static String getDateTimeYYYYMMDD(String format, String str) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        boolean result = true;
        try{
            date = df.parse(str);
        }catch(ParseException e){
            result = false;
        }
        if(result){
            return dateFormat.format(date);
        }else{
            return null;
        }
    }

    public static String getDateTimeYYYYMM(String format, String str) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        DateFormat df = new SimpleDateFormat("yyyyMM");
        Date date = null;
        boolean result = true;
        try{
            date = df.parse(str);
        }catch(ParseException e){
            result = false;
        }
        if(result){
            return dateFormat.format(date);
        }else{
            return null;
        }
    }
}