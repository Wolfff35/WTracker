package com.wolff.wtracker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.wolff.wtracker.tools.Debug;

/**
 * Created by wolff on 13.09.2017.
 */

public class WTrackerServise extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        //super.onCreate();
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("MY NOTIF");
        Notification notification;
        if (Build.VERSION.SDK_INT < 16)
            notification = builder.getNotification();
        else
            notification = builder.build();
        startForeground(777, notification);
        Debug.Log("SERVICE","onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        getCoordinates();
        //return super.onStartCommand(intent, flags, startId);
        Debug.Log("SERVICE","onStartCommand");
        return START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        Debug.Log("SERVICE","onDestroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Debug.Log("SERVICE","onTaskRemoved");
        if (Build.VERSION.SDK_INT == 19)
        {
            Intent restartIntent = new Intent(this, getClass());

            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent pi = PendingIntent.getService(this, 1, restartIntent,
                    PendingIntent.FLAG_ONE_SHOT);
            restartIntent.putExtra("RESTART",true);
            am.setExact(AlarmManager.RTC, System.currentTimeMillis() + 3000, pi);
        }
    }
    void getCoordinates() {
    }
}
