package com.wolff.wtracker.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.wolff.wtracker.R;
import com.wolff.wtracker.tools.Debug;


/**
 * Created by wolff on 05.10.2017.
 */

public class Google_map_fragment extends Fragment implements OnMapReadyCallback {
    public GoogleMap mMap;
    private MapView mMapView;

    private static final String LOG_TAG = "Google_map_fragment";
    public static Google_map_fragment newInstance(){
         Google_map_fragment fragment = new Google_map_fragment();
        return fragment;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Debug.Log(LOG_TAG,"onMapReady = READY!!!");
        mMap = googleMap;
        setupMap();
     }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
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

        // Access to the location has been granted to the app.
        //TODO if (mMap != null&&PermissionTools.enableMyLocation((AppCompatActivity) getActivity())) mMap.setMyLocationEnabled(true);
        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }



 }
