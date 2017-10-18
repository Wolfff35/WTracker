package com.wolff.wtracker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.wolff.wtracker.localdb.DataLab;
import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.online.AsyncInsertCoords;
import com.wolff.wtracker.tools.Debug;
import com.wolff.wtracker.tools.OtherTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.wolff.wtracker.tools.PreferencesTools.IS_DEBUG;

/**
 * Created by wolff on 13.09.2017.
 */

public class WTrackerServise extends Service {
    private static final int REPEAT_TIME = 1;
    private LocationService mLocationService;
    private WUser mCurrentUser;
    //private Map<WUser, WCoord> mLastUserCoordinates = new HashMap<>();
    private ArrayList<WUser> mUsers;
    private String mPhoneIMEI;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        DataLab dataLab = DataLab.get(getApplicationContext());
        mUsers = dataLab.getWUserList();
        mCurrentUser = dataLab.getCurrentUser(mUsers);
        //mLastUserCoordinates = dataLab.getLastCoords(mUsers);
        mLocationService = LocationService.getLocationManager(getApplicationContext(), mCurrentUser);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Looking for " + mCurrentUser.get_id_user());
        Notification notification;
        if (Build.VERSION.SDK_INT < 16)
            notification = builder.getNotification();
        else
            notification = builder.build();
        startForeground(777, notification);
        Debug.Log("SERVICE", "onCreate");

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        sendCoordsToServer();
        Debug.Log("SERVICE", "onStartCommand");
        return START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        mLocationService = null;
        Debug.Log("SERVICE", "onDestroy");

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Debug.Log("SERVICE", "onTaskRemoved");
        mLocationService = null;
        if (Build.VERSION.SDK_INT == 19) {
            Intent restartIntent = new Intent(this, getClass());

            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent pi = PendingIntent.getService(this, 1, restartIntent,
                    PendingIntent.FLAG_ONE_SHOT);
            restartIntent.putExtra("RESTART", true);
            am.setExact(AlarmManager.RTC, System.currentTimeMillis() + 3000, pi);
        }
    }

    private void sendCoordsToServer() {
        final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(3);
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                if (!isOnline()) {
                    return;
                }
                ArrayList<WCoord> coords = DataLab.get(getApplicationContext()).getLocalCoords();
                AsyncInsertCoords task = new AsyncInsertCoords(getApplicationContext(), mCurrentUser, coords);
                task.execute();

            }
        }, 0, REPEAT_TIME, TimeUnit.MINUTES);
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting());
    }


}
