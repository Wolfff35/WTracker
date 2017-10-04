package com.wolff.wtracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wolff.wtracker.localdb.DataLab;
import com.wolff.wtracker.online.AsyncExecute;
import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.online.AsyncInsertUser;
import com.wolff.wtracker.online.DbSchemaOnline;
import com.wolff.wtracker.online.OnlineDataLab;
import com.wolff.wtracker.tools.Debug;
import com.wolff.wtracker.tools.OtherTools;
import com.wolff.wtracker.tools.PermissionTools;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.wolff.wtracker.tools.PreferencesTools.IS_DEBUG;

public class ActivityMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private Map<WUser,WCoord> mLastUserCoordinates = new HashMap<>();
    private ArrayList<WUser> mUsers = new ArrayList<>();
    private WUser mCurrentUser;
    private Map<WUser,Marker> mMarkers = new HashMap<>();

    private LinearLayout mContainer_info;
    private TextView tvInfo;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;

    private boolean mPermissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
      DataLab dataLab = DataLab.get(getApplicationContext());
        //OnlineDataLab onlineDataLab = OnlineDataLab.get(getApplicationContext());
        //registerUser();
        mUsers = dataLab.getWUserList();
        mLastUserCoordinates = dataLab.getLastCoords(mUsers);
        mCurrentUser = dataLab.getCurrentUser(mUsers);
        if (mCurrentUser == null) {
            registerUser();
        }

        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.item_container, mMapFragment);
        ft.commit();
        mMapFragment.getMapAsync(this);

        mContainer_info = (LinearLayout)findViewById(R.id.container_info);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        if (!OtherTools.isServiceRunning(getApplicationContext(), WTrackerServise.class)
                &&mCurrentUser!=null) {
            Intent intent = new Intent(getApplicationContext(), WTrackerServise.class);
            startService(intent);
            Debug.Log("RUN", "Servise!");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_uprate_map: {
                mLastUserCoordinates = DataLab.get(getApplicationContext()).getLastCoords(mUsers);
                drawLastCoords();
                break;
            }
            case R.id.action_register: {
                break;
            }
            case R.id.action_log: {
                logData();
                break;
            }
            case R.id.action_write_coords_to_server: {

                OnlineDataLab onlineDataLab = OnlineDataLab.get(getApplicationContext());
                ArrayList<WCoord> coords = DataLab.get(getApplicationContext()).getLocalCoords();
                onlineDataLab.writeCoordsToServer(mCurrentUser,coords);
                break;
            }
            case R.id.action_create_online_tables: {
                try {
                    AsyncExecute ac = new AsyncExecute(getApplicationContext());
                    boolean flag = ac.execute(DbSchemaOnline.CREATE_TABLE_USERS).get();
                    Debug.Log("AsyncExecute","flag users = "+flag);
                    AsyncExecute ac2 = new AsyncExecute(getApplicationContext());
                    boolean flag2 = ac2.execute(DbSchemaOnline.CREATE_TABLE_COORDS).get();
                    Debug.Log("AsyncExecute","flag coords = "+flag2);
                } catch (InterruptedException e) {
                    Debug.Log("AsyncExecute","ERROR 1 "+e.getLocalizedMessage());
                } catch (ExecutionException e) {
                    Debug.Log("AsyncExecute","ERROR 2 "+e.getLocalizedMessage());
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupMap();
        Debug.Log("onMapReady","READY!!!");
    }
    private void setupMap() {
        enableMyLocation();
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
            tvInfo.setText("lat: "+coord.get_coord_lat()+"; lng: "+coord.get_coord_lon());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
            Debug.Log("SHOW",""+user.get_id_user());
        }

    }
    private void registerUser() {
        if (IS_DEBUG&&mCurrentUser==null) {

            WUser currentUser = new WUser();
            currentUser.set_id_user("380996649531");
            currentUser.set_currentUser(true);
            currentUser.set_imei_phone("01010100000000001");
            currentUser.set_pin_for_access("1234");
            currentUser.set_name("Wolfff");
            currentUser.set_password("passs");
            DataLab dataLab = DataLab.get(getApplicationContext());
            dataLab.user_add(currentUser);
            mUsers = dataLab.getWUserList();
            mLastUserCoordinates = dataLab.getLastCoords(mUsers);
            mCurrentUser = dataLab.getCurrentUser(mUsers);

            //mCurrentUser=currentUser;
            WCoord coord = new WCoord();
            coord.set_date(new Date());
            coord.set_coord_lat(20);
            coord.set_coord_lon(13);
            coord.set_accuracy(0);
            coord.set_altitude(0);
            coord.set_provider("gps");
            dataLab.last_coord_add(mCurrentUser,coord);
            try {
                boolean fl = new AsyncInsertUser(getApplicationContext(),mCurrentUser).execute().get();
                Debug.Log("register","user SUCCESS");
            } catch (InterruptedException e) {
                Debug.Log("register","user ERROR 1"+e.getLocalizedMessage());
            } catch (ExecutionException e) {
                Debug.Log("register","user ERROR 2"+e.getLocalizedMessage());
            }

            Debug.Log("REGISTER", "USER CURRENT----------------------------");
        }
    }
    private void logData(){
        Debug.Log("START","=========================================================================");
        Debug.Log("mCurrentUser"," = "+mCurrentUser.get_id_user());
        for(int i=0;i<mUsers.size();i++) {
            Debug.Log("mUsers", "i="+i+" - "+ mUsers.get(i));
        }
        for(Map.Entry<WUser,WCoord> entry:mLastUserCoordinates.entrySet()) {
            Debug.Log("mLAstCoords", "user-"+entry.getKey().toString()+"; coord - "+entry.getValue().toString());
        }
        //Debug.Log("","");
        Debug.Log("END","=========================================================================");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE
                |requestCode != WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionTools.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)
                && PermissionTools.isPermissionGranted(permissions,grantResults,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }
    private void showMissingPermissionError() {
        PermissionTools.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }
    private void enableMyLocation() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED))
        {
            // Permission to access the location is missing.
            PermissionTools.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
         } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }
}
//https://habrahabr.ru/post/257443/
//https://developers.google.com/maps/documentation/android-api/start?hl=ru