package com.wolff.wtracker.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wolff.wtracker.R;
import com.wolff.wtracker.localdb.DataLab;
import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.tools.Debug;
import com.wolff.wtracker.tools.PermissionTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.wolff.wtracker.ActivityMain.LOCATION_PERMISSION_REQUEST_CODE;


/**
 * Created by wolff on 05.10.2017.
 */

public class Google_map_fragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private MapView mMapView;

    private Map<WUser,WCoord> mLastUserCoordinates = new HashMap<>();
    private ArrayList<WUser> mUsers = new ArrayList<>();
    private WUser mCurrentUser;
    private Map<WUser,Marker> mMarkers = new HashMap<>();

    private boolean mPermissionDenied = false;

    private static final String LOG_TAG = "Google_map_fragment";
    public static Google_map_fragment newInstance(){
        return new Google_map_fragment();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Debug.Log(LOG_TAG,"onMapReady = READY!!!");
        mMap = googleMap;
        DataLab dataLab = DataLab.get(getContext());
        mUsers = dataLab.getWUserList();
        mLastUserCoordinates = dataLab.getLastCoords(mUsers);
        mCurrentUser = dataLab.getCurrentUser(mUsers);
        setupMap();
     }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
         setHasOptionsMenu(true);
        Debug.Log(LOG_TAG,"onCreate");
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        Debug.Log(LOG_TAG,"onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        Debug.Log(LOG_TAG,"onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        Debug.Log(LOG_TAG,"onDestroy");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
        Debug.Log(LOG_TAG,"onLowMemory");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
       super.onCreateView(inflater, viewGroup, bundle);
        View view = inflater.inflate(R.layout.google_map_fragment, viewGroup, false);
        mMapView = (MapView)view.findViewById(R.id.mapView);
        mMapView.onCreate(bundle);
        mMapView.getMapAsync(this);
        Debug.Log(LOG_TAG,"onCreateView");
        return view;
    }
    private void setupMap() {
        Debug.Log(LOG_TAG,"setupMap BEGIN!!!");
        enableMyLocation((AppCompatActivity) getActivity());
        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        drawLastCoords();
    }
    private void drawLastCoords(){
        Debug.Log(LOG_TAG,"drawLastCoords BEGIN!!!");
        for (Map.Entry<WUser, WCoord> entry : mLastUserCoordinates.entrySet()) {
            WCoord coord = entry.getValue();
            WUser user = entry.getKey();
            LatLng ll = new LatLng(coord.get_coord_lat(),coord.get_coord_lon());
            if(user==null){
                Debug.Log("drawLastCoords","NULL USER!!");
                return;
            }
            if(mMarkers.get(user)!=null){
                mMarkers.get(user).setPosition(ll);
                mMarkers.get(user).setTitle(user.get_name()+" "+user.get_id_user());
                mMarkers.get(user).setSnippet("lat: "+coord.get_coord_lat()+";  lng: "+coord.get_coord_lon());
                Debug.Log("MARKER","UPDATE");
            }else {
                MarkerOptions mo = new MarkerOptions();
                mo.position(ll);
                mo.title(user.get_name()+" "+user.get_id_user());
                mo.snippet("lat: "+coord.get_coord_lat()+";  lng: "+coord.get_coord_lon());
                //mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
                Marker marker = mMap.addMarker(mo);
                mMarkers.put(user,marker);
                Debug.Log("MARKER","ADD");
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
            Debug.Log("SHOW",""+user.get_id_user());
        }

    }
    private void enableMyLocation(AppCompatActivity activity) {
        Debug.Log(LOG_TAG,"enableMyLocation");
        if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED))
        {
            // Permission to access the location is missing.
            PermissionTools.requestPermission(activity, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
         } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }

    }
}
