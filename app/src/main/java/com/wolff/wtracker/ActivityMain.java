package com.wolff.wtracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wolff.wtracker.localdb.DataLab;
import com.wolff.wtracker.online.OnlineDataLab;
import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.tools.Debug;
import com.wolff.wtracker.tools.OtherTools;
import com.wolff.wtracker.tools.PermissionTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.wolff.wtracker.tools.PermissionTools.PERMISSION_REQUEST_CODE;
import static com.wolff.wtracker.tools.PreferencesTools.IS_DEBUG;

public class ActivityMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private Map<WUser,WCoord> mLastUserCoordinates = new HashMap<>();
    private ArrayList<WUser> mUsers = new ArrayList<>();
    private WUser mCurrentUser;
    private Map<WUser,Marker> mMarkers = new HashMap<>();
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

        //================
        DataLab dataLab = DataLab.get(getApplicationContext());
        OnlineDataLab onlineDataLab = OnlineDataLab.get(getApplicationContext());

        mUsers = dataLab.getWUserList();
        mLastUserCoordinates = dataLab.getLastCoords(mUsers);
        mCurrentUser = dataLab.getCurrentUser(mUsers);
        if (mCurrentUser == null) {
            registerUser();
        }

        PermissionTools permissionTools = new PermissionTools();
        if (!permissionTools.hasPermissions(getApplicationContext())) {
            permissionTools.requestPermissionWithRationale(this);
        }
        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.item_container, mMapFragment);
        ft.commit();
        mMapFragment.getMapAsync(this);


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
        if (id == R.id.action_settings) {
            mLastUserCoordinates=DataLab.get(getApplicationContext()).getLastCoords(mUsers);
            drawLastCoords();
                return true;
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
        if (new PermissionTools().hasPermissions(getApplicationContext())) {
            setupMap();
            Debug.Log("onMapReady","Ready & has permissions");
        }
        Debug.Log("onMapReady","READY!!!");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allowed = true;

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:

                for (int res : grantResults) {
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed) {
            //user granted all permissions we can perform our task.
            setupMap();
        } else {
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean showReq = false;
                for (int i = 0; i < permissions.length; i++) {
                    if (shouldShowRequestPermissionRationale(permissions[i])) {
                        showReq = true;
                    }
                }
                if (showReq) {
                    Toast.makeText(this, "Permissions denied.", Toast.LENGTH_LONG).show();
                    // } else {
                    PermissionTools permissionTools = new PermissionTools();
                    permissionTools.showPermissionSnackbar(this);
                }
            }
        }
    }

    private void setupMap() {
        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        drawLastCoords();
    }

    private void drawLastCoords(){
        //mLastUserCoordinates = DataLab.get(getApplicationContext()).getLastCoords();
        for (Map.Entry<WUser, WCoord> entry : mLastUserCoordinates.entrySet()) {
            WCoord coord = entry.getValue();
            WUser user = entry.getKey();
            LatLng ll = new LatLng(coord.get_coord_lat(),coord.get_coord_lon());
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
                mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
                Marker marker = mMap.addMarker(mo);
                mMarkers.put(user,marker);
                Debug.Log("MARKER","ADD");
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
            Debug.Log("SHOW",""+user.get_id_user());
        }

    }
    private void registerUser() {
        if (IS_DEBUG) {
            WUser currentUser = new WUser();
            currentUser.set_id_user("380996649531");
            currentUser.set_currentUser(true);
            DataLab dataLab = DataLab.get(getApplicationContext());
            dataLab.user_add(currentUser);
            mCurrentUser=currentUser;
            Debug.Log("REGISTER", "USER CURRENT----------------------------");
        }
    }
}
//https://habrahabr.ru/post/257443/
//https://developers.google.com/maps/documentation/android-api/start?hl=ru