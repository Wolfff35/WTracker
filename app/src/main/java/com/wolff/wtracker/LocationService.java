package com.wolff.wtracker;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.wolff.wtracker.localdb.DataLab;
import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.tools.Debug;

import static android.location.LocationProvider.AVAILABLE;

/**
 * Created by wolff on 28.09.2017.
 */

public class LocationService implements LocationListener {
    private Context mContext;
    private WUser mCurrentUser;
    //The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 10 meters

    //The minimum time beetwen updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute

    //private final static boolean forceNetwork = false;

    private static LocationService instance = null;

    private LocationManager locationManager;
    private Location mLocation;
    //private double longitude;
    //private double latitude;

    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    //private boolean locationServiceAvailable;

    /**
     * Singleton implementation
     *
     * @return
     */
    public static LocationService getLocationManager(Context context, WUser currentUser) {
        if (instance == null) {
            instance = new LocationService(context, currentUser);
        }
        return instance;
    }

    /**
     * Local constructor
     */
    private LocationService(Context context, WUser currentUser) {
        mContext = context;
        mCurrentUser = currentUser;
        initLocationService();
        Debug.Log("SERV", "LocationService created");
    }


    /**
     * Sets up location service after permissions is granted
     */
    @TargetApi(23)
    private void initLocationService() {

        this.locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        // Get GPS and network status
        this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        updateCoords();
    }


    @Override
    public void onLocationChanged(Location location) {
        // do stuff here with location object
        updateCoords();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (provider){
            case LocationManager.GPS_PROVIDER: {
                isGPSEnabled = (status == AVAILABLE);

                break;
            }
            case LocationManager.NETWORK_PROVIDER: {
                isNetworkEnabled = (status == AVAILABLE);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        setFlags(provider,true);
    }

    @Override
    public void onProviderDisabled(String provider) {
        setFlags(provider,false);
    }

    private void setFlags(String provider,boolean flag){
        switch (provider){
            case LocationManager.GPS_PROVIDER: {
                isGPSEnabled = flag;
                break;
            }
            case LocationManager.NETWORK_PROVIDER: {
                isNetworkEnabled = flag;
                break;
            }
            default:
                break;
        }

    }
    private void updateCoords() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (isGPSEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            if (locationManager != null) {
                mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                //updateCoordinates();
            }
        } else if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            if (locationManager != null) {
                mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                //updateCoordinates();
            }
        }//end if
        writeToLocalDB();
        Debug.Log("UPDATE", "COORDS: lat: " + mLocation.getLatitude() + "; lon: " + mLocation.getLongitude() + "; provider = " + mLocation.getProvider());
        Debug.Log("UPDATE", "COORDS: accuracy: " + mLocation.getAccuracy() + "; altitude: " + mLocation.getAltitude() + "; bearing = " + mLocation.getBearing());
        Debug.Log("UPDATE", "COORDS: ElapsedRealtimeNanos: " + mLocation.getElapsedRealtimeNanos() + "; speed: " + mLocation.getSpeed() + "; time = " + mLocation.getTime());
        Debug.Log("=", "================================================================================");
    }

    private void writeToLocalDB() {
        DataLab dataLab = DataLab.get(mContext);
        WCoord coord = new WCoord(mLocation, mCurrentUser);
        dataLab.coord_add(coord);
        int l = dataLab.last_coord_update(coord);
        if (l == 0) {
            dataLab.last_coord_add(coord);
        }
    }
}