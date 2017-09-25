package com.wolff.wtracker.tools;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by wolff on 24.09.2017.
 */

public class OtherTools {
    public static boolean isServiceRunning(Context context,Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
