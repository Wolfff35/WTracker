package com.wolff.wtracker.tools;

import android.text.format.Time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * Created by wolff on 30.08.2017.
 */

public class DateFormatTools {
    //public  static final String DATE_FORMAT_SQL_SHORT = "yyyy-MM-dd";
    public  static final String TIME_FORMAT_SHORT = "HH:mm:ss";
    //public static final String DATE_FORMAT_DELETE = "%Y.%m.%d %H:%M:%S";
    //public  static final String DATE_FORMAT_SQL = "yyyy-MM-dd HH:mm:ss";
    public  static final String DATE_FORMAT_STR = "yyyy-MM-dd'T'HH:mm:ss";
    public  static final String DATE_FORMAT_SHORT = "yyyy/MM/dd";
    public  static final String TIME_FORMAT_SQL_LONG = "HH:mm:ss.SSS";
    public static final String DATE_FORMAT_VID = "dd-MM-yyyy";
    public static final String DATE_FORMAT_VID_FULL = "dd-MM-yyyy HH:mm:ss";
    public static final String DATE_FORMAT_SAVE = "yyyy-MM-dd HH:mm:ss";

    public Date dateFromString(String strDate, String strFormat){
        if(strDate==null){
            return null;
        }
        if(strDate.equalsIgnoreCase("")|strDate.isEmpty()){
            return null;
        }
        DateFormat format = new SimpleDateFormat(strFormat, Locale.ENGLISH);
        try {
            return format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String dateToString(Date date,String strFormat){
        Date locDate;
        if(date==null){
            locDate = dateFromString("0001-01-01T00:00:00", DATE_FORMAT_STR);
        }else {
            locDate=date;
        }

        DateFormat format = new SimpleDateFormat(strFormat, Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("GMT+02"));//TODO
        return format.format(locDate);
    }
    public String addZero(int num){
        if(String.valueOf(num).length()==1){
            return "0"+String.valueOf(num);
        }
        return String.valueOf(num);
    }

    public Date getDate(int year,int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, day);
        return calendar.getTime();

    }
}
