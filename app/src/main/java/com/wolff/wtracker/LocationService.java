package com.wolff.wtracker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.wolff.wtracker.localdb.DataLab;
import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.online.OnlineDataLab;
import com.wolff.wtracker.tools.DateFormatTools;
import com.wolff.wtracker.tools.Debug;
import com.wolff.wtracker.tools.PermissionTools;
import com.wolff.wtracker.tools.PreferencesTools;

import static android.location.LocationProvider.AVAILABLE;
import static com.wolff.wtracker.tools.PreferencesTools.MIN_DISTANCE_CHANGE_FOR_UPDATES;
import static com.wolff.wtracker.tools.PreferencesTools.MIN_TIME_FOR_UPDATES;

/**
 * Created by wolff on 28.09.2017.
 */

public class LocationService implements LocationListener {
    private Context mContext;
    private WUser mCurrentUser;
    private WCoord mLastCoord;
    //The minimum distance to change updates in meters
    //private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 20; // 20 meters

    //The minimum time beetwen updates in milliseconds
    //private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute

    private static LocationService instance = null;

    private LocationManager locationManager;
    private Location mLocation;

    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;

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
        mLastCoord = OnlineDataLab.get(mContext).getOnlineUserLastCoord(mCurrentUser);
        initLocationService();
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
        mLocation=location;
        writeToLocalDB();
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
        int distance_change = new PreferencesTools().getIntPreference(mContext,MIN_DISTANCE_CHANGE_FOR_UPDATES);
        int time_change = new PreferencesTools().getIntPreference(mContext,MIN_TIME_FOR_UPDATES)*1000*60;

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (isGPSEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    time_change,
                    distance_change, this);

            if (locationManager != null) {
                mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        } else if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    time_change,
                    distance_change, this);
            if (locationManager != null) {
                mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }//end if
        if(mLocation!=null) {
            writeToLocalDB();
        }
    }

    private void writeToLocalDB() {
        DataLab dataLab = DataLab.get(mContext);
        WCoord coord = new WCoord(mLocation);
        boolean write = false;
        DateFormatTools dft = new DateFormatTools();
        if(mLastCoord!=null){
            if(mLastCoord.get_coord_lon()!=coord.get_coord_lon()|
                    mLastCoord.get_coord_lat()!=coord.get_coord_lat()|
                    mLastCoord.get_altitude()!=coord.get_altitude()|
                    mLastCoord.get_accuracy()!=coord.get_accuracy()|
                    !dft.dateToString(mLastCoord.get_date(),DateFormatTools.DATE_FORMAT_SHORT)
                            .equals(dft.dateToString(coord.get_date(),DateFormatTools.DATE_FORMAT_SHORT))){
                write=true;
            }
        }else {
            write=true;
        }
        if(write) {
            dataLab.coord_add(mCurrentUser, coord);
             mLastCoord=coord;
        }
    }
}
