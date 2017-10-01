package com.wolff.wtracker;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.wolff.wtracker.localdb.DataLab;
import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.tools.Debug;
import com.wolff.wtracker.tools.PermissionTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.wolff.wtracker.tools.PreferencesTools.IS_DEBUG;

/**
 * Created by wolff on 13.09.2017.
 */

public class WTrackerServise extends Service {
    //private LocationManager locationManager;
    LocationService mLocationService;
    WUser mCurrentUser;
    //ArrayList<WCoord> mLastUserCoordinates = new ArrayList<>();
    private Map<WUser,WCoord> mLastUserCoordinates = new HashMap<>();
    ArrayList<WUser> mUsers = new ArrayList<>();

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
        Debug.Log("SERVICE", "onCreate");
        //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (IS_DEBUG){
            mCurrentUser=new WUser();
            mCurrentUser.set_id_user("380996649531");

        }
        DataLab dataLab = DataLab.get(getApplicationContext());
        mLastUserCoordinates = dataLab.getLastCoords(mUsers);
        mLocationService = LocationService.getLocationManager(getApplicationContext(),mCurrentUser);

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        //    sendCoordsToServer();
        //return super.onStartCommand(intent, flags, startId);
        Debug.Log("SERVICE", "onStartCommand");
        return START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        mLocationService=null;
        Debug.Log("SERVICE", "onDestroy");

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Debug.Log("SERVICE", "onTaskRemoved");
        mLocationService=null;
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


    }


}
