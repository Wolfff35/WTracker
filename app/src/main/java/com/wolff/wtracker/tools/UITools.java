package com.wolff.wtracker.tools;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wolff.wtracker.R;
import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.online.OnlineDataLab;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wolff on 05.10.2017.
 */

public class UITools {
    public void displayFragment(AppCompatActivity activity, Fragment currentFragment) {
        FragmentTransaction fragmentTransaction;
        FragmentManager fm = activity.getSupportFragmentManager();

        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.item_container, currentFragment);
        fragmentTransaction.commit();
    }

    public Polyline drawUserWay(Context context,GoogleMap map, WUser user, Date currentDate){
        Polyline line=null;
        ArrayList<WCoord> userCoords = OnlineDataLab.get(context).getOnlineUsersCoordinates(user,currentDate);
        if(userCoords.size()>0){
            double minLat=180,minLon=180,maxLat=-180,maxLon=-180;
            PolylineOptions opt = new PolylineOptions();
            for(int i=0;i<userCoords.size();i++){
                double currLat = userCoords.get(i).get_coord_lat();
                double currLon = userCoords.get(i).get_coord_lon();
                opt.add(new LatLng(currLat,currLon));
                if(currLat<minLat) minLat=currLat;
                if(currLat>maxLat) maxLat=currLat;
                if(currLon<minLon) minLon=currLon;
                if(currLon>maxLon) maxLon=currLon;

            }
            opt.color(Color.MAGENTA);
            line = map.addPolyline(opt);
            LatLngBounds AREA = new LatLngBounds(new LatLng(minLat,minLon),new LatLng(maxLat,maxLon));
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(AREA,0));
        }
        return line;
    }
    public Map<WUser,Marker> drawLastCoords(GoogleMap map,Map<WUser,WCoord>lastUserCoordinates){
        Map<WUser,Marker> mMarkers = new HashMap<>();
        for (Map.Entry<WUser, WCoord> entry : lastUserCoordinates.entrySet()) {
            WCoord coord = entry.getValue();
            WUser user = entry.getKey();
            LatLng ll = new LatLng(coord.get_coord_lat(),coord.get_coord_lon());
            if(user==null){
                return mMarkers;
            }
            if(mMarkers.get(user)!=null){
                mMarkers.get(user).setPosition(ll);
                mMarkers.get(user).setTitle(user.get_name()+" "+user.get_id_user());
                mMarkers.get(user).setSnippet("lat: "+coord.get_coord_lat()+";  lng: "+coord.get_coord_lon());
            }else {
                MarkerOptions mo = new MarkerOptions();
                mo.position(ll);
                mo.title(user.get_name()+" "+user.get_id_user());
                mo.snippet("lat: "+coord.get_coord_lat()+";  lng: "+coord.get_coord_lon());
                //mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
                Marker marker = map.addMarker(mo);
                mMarkers.put(user,marker);
            }

            CameraPosition position = new CameraPosition(ll,10,0,0);
            map.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        }
        return mMarkers;
    }

}
