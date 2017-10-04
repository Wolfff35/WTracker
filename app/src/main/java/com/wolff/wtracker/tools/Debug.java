
package com.wolff.wtracker.tools;

import android.content.Context;
import android.util.Log;

import java.util.Date;

/**
 * Created by wolff on 31.08.2017.
 */

public class Debug {
    private static Date startTime;
    private static String tagg;

    public static void start(String tag) {
        startTime = new Date();
        tagg = tag;
        Log.e("DEBUG", "BEGIN " + tag + " " + new DateFormatTools().dateToString(startTime, DateFormatTools.DATE_FORMAT_VID_FULL));

    }

    public static void stop() {
        Date endDate = new Date();
        Log.e("DEBUG", "END " + tagg + " " + new DateFormatTools().dateToString(endDate, DateFormatTools.DATE_FORMAT_VID_FULL) + "; time - " + ((endDate.getTime() - startTime.getTime())) + " m/sec");

    }

    public static void Log(String tag, String msg) {
        if (PreferencesTools.IS_DEBUG) {
            Log.e(tag, msg);
        }
    }

    //======================================

}
