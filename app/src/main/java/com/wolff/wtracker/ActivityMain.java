package com.wolff.wtracker;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.wolff.wtracker.fragments.Add_user_fragment;
import com.wolff.wtracker.fragments.Google_map_fragment;
import com.wolff.wtracker.fragments.Register_user_fragment;
import com.wolff.wtracker.localdb.DataLab;
import com.wolff.wtracker.online.AsyncExecute;
import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.online.AsyncInsertUser;
import com.wolff.wtracker.online.AsyncRequestUser;
import com.wolff.wtracker.online.DbSchemaOnline;
import com.wolff.wtracker.online.OnlineDataLab;
import com.wolff.wtracker.tools.Debug;
import com.wolff.wtracker.tools.OtherTools;
import com.wolff.wtracker.tools.PermissionTools;


import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static com.wolff.wtracker.tools.PreferencesTools.IS_DEBUG;

public class ActivityMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        Register_user_fragment.Register_user_fragment_listener,
        Add_user_fragment.Add_user_fragment_listener{

    private Fragment mCurrentFragment;
    private ArrayList<WUser> mUsers = new ArrayList<>();
    private WUser mCurrentUser;
    private boolean mPermissionDenied = false;

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;
    private static final int READ_PHONE_STATE_PERMISSION_REQUEST_CODE = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DataLab dataLab = DataLab.get(getApplicationContext());
        mUsers = dataLab.getWUserList();
        mCurrentUser = dataLab.getCurrentUser(mUsers);
        registerOrLoginUser();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                new OtherTools().createDrawerMenu(mUsers,navigationView.getMenu());
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        displayFragment();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
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
            case R.id.action_add_user: {
                mCurrentFragment = Add_user_fragment.newInstance();
                displayFragment();
                break;
            }
            case R.id.action_uprate_map: {
                //mLastUserCoordinates = DataLab.get(getApplicationContext()).getLastCoords(mUsers);
                //drawLastCoords();
                break;
            }
            case R.id.action_stop_service: {
                Intent intent = new Intent(getApplicationContext(), WTrackerServise.class);
                stopService(intent);
                Debug.Log("STOP", "Servise!");
                break;
            }
            case R.id.action_start_service: {
                Intent intent = new Intent(getApplicationContext(), WTrackerServise.class);
                startService(intent);
                Debug.Log("STOP", "Servise!");
                break;
            }
            case R.id.action_write_coords_to_server: {

                OnlineDataLab onlineDataLab = OnlineDataLab.get(getApplicationContext());
                ArrayList<WCoord> coords = DataLab.get(getApplicationContext()).getLocalCoords();
                onlineDataLab.writeCoordsToServer(mCurrentUser, coords);
                break;
            }
            case R.id.action_create_online_tables: {
                try {
                    AsyncExecute ac = new AsyncExecute(getApplicationContext());
                    boolean flag = ac.execute(DbSchemaOnline.CREATE_TABLE_USERS).get();
                    Debug.Log("AsyncExecute", "flag users = " + flag);
                    AsyncExecute ac2 = new AsyncExecute(getApplicationContext());
                    boolean flag2 = ac2.execute(DbSchemaOnline.CREATE_TABLE_COORDS).get();
                    Debug.Log("AsyncExecute", "flag coords = " + flag2);
                } catch (InterruptedException e) {
                    Debug.Log("AsyncExecute", "ERROR 1 " + e.getLocalizedMessage());
                } catch (ExecutionException e) {
                    Debug.Log("AsyncExecute", "ERROR 2 " + e.getLocalizedMessage());
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Debug.Log("NAVIGATION","id = "+id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void showMissingPermissionError() {
        PermissionTools.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE
                | requestCode != WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
                | requestCode != READ_PHONE_STATE_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionTools.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)
                && PermissionTools.isPermissionGranted(permissions, grantResults, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && PermissionTools.isPermissionGranted(permissions, grantResults, Manifest.permission.READ_PHONE_STATE)
                ) {
            // Enable the my location layer if the permission has been granted.
            //enableMyLocation((AppCompatActivity) getActivity());
            //drawLastCoords();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
            Debug.Log("", "onRequestPermissionsResult");
        }
    }

    private void registerOrLoginUser(){
        mCurrentFragment = Register_user_fragment.newInstance();
        try {
            WUser onlineUser = null;
            if (mCurrentUser != null) {
                onlineUser = new AsyncRequestUser(getApplicationContext(), mCurrentUser).execute().get();
            }
            if (mCurrentUser != null &&onlineUser!=null) {
                  if(mCurrentUser.equals(onlineUser)){
                    Debug.Log("registerOrLogin","есть и на сервере и локально, ВСЕ СОВПАДАЕТ");
                    mCurrentFragment = Google_map_fragment.newInstance(null);
                    if (!OtherTools.isServiceRunning(getApplicationContext(), WTrackerServise.class)
                            && mCurrentUser != null) {
                        Intent intent = new Intent(getApplicationContext(), WTrackerServise.class);
                        startService(intent);
                        Debug.Log("RUN", "Servise!");
                    }
                }else {
                    Debug.Log("registerOrLogin","есть и на сервере и локально,  но что-то НЕ СОВПАДАЕТ");
                }
            } else if(mCurrentUser==null&&onlineUser!=null){
            //есть на сервере, нет локально
                Debug.Log("registerOrLogin","есть на сервере, нет локально");
            }else if(mCurrentUser!=null&&onlineUser==null){
                //есть локально, нет онлайн
                Debug.Log("registerOrLogin","есть локально, нет онлайн");

            }else if(mCurrentUser==null&&onlineUser==null){
                //есть локально, нет онлайн
                Debug.Log("registerOrLogin","нет локально, нет онлайн");

            }
        } catch (InterruptedException e) {
            Debug.Log("GET USER", "ERROR 1 " + e.getLocalizedMessage());
        } catch (ExecutionException e) {
            Debug.Log("GET USER", "ERROR 2 " + e.getLocalizedMessage());
        }
    }

    private void displayFragment() {
        FragmentTransaction fragmentTransaction;
        FragmentManager fm = getSupportFragmentManager();

        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.item_container, mCurrentFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onClickButtonRegisterLoginUser(String buttonType, WUser user) {
        Debug.Log("CLICK", " button " + buttonType);
        if (buttonType.equals("REGISTER")) {
            if (new OtherTools().registerNewUser(getApplicationContext(), user)) {
                ((TextView) mCurrentFragment.getView().findViewById(R.id.tvInfoMessage))
                        .setText("Регистрация успешна!");
                Debug.Log("REG", "OK");
                mCurrentFragment = Google_map_fragment.newInstance(null);
                displayFragment();
            } else {
                Debug.Log("REG", "ERROR");
                ((TextView) mCurrentFragment.getView().findViewById(R.id.tvInfoMessage))
                        .setText("Ошибка регистрации!");
            }
        } else if (buttonType.equals("LOGIN")) {
            if (new OtherTools().loginUser(getApplicationContext(), user)) {
                mCurrentFragment = Google_map_fragment.newInstance(null);
                displayFragment();

            }

        }
    }

    @Override
    public void onClickButtonAddUser(WUser user) {
        if (new OtherTools().addUser(getApplicationContext(), user)) {
            ((TextView) mCurrentFragment.getView().findViewById(R.id.tvInfoMessage))
                    .setText("Пользователь добавлен успешно!");
            Debug.Log("REG", "OK");
            //mCurrentFragment = Google_map_fragment.newInstance();
            //displayFragment();
        } else {
            Debug.Log("REG", "ERROR");
            ((TextView) mCurrentFragment.getView().findViewById(R.id.tvInfoMessage))
                    .setText("Ошибка добавления!");
        }

    }
}

//https://habrahabr.ru/post/257443/
//https://developers.google.com/maps/documentation/android-api/start?hl=ru