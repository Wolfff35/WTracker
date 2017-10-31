package com.wolff.wtracker;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.wolff.wtracker.fragments.Add_user_fragment;
import com.wolff.wtracker.fragments.Google_map_fragment;
import com.wolff.wtracker.fragments.Register_user_fragment;
import com.wolff.wtracker.localdb.DataLab;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.online.DbSchemaOnline;
import com.wolff.wtracker.online.OnlineDataLab;
import com.wolff.wtracker.tools.DateFormatTools;
import com.wolff.wtracker.tools.Debug;
import com.wolff.wtracker.tools.OtherTools;
import com.wolff.wtracker.tools.PermissionTools;
import com.wolff.wtracker.tools.UITools;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static com.wolff.wtracker.tools.PermissionTools.MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION;
import static com.wolff.wtracker.tools.PermissionTools.MY_PERMISSION_REQUEST_READ_PHONE_STATE;

public class ActivityMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        Register_user_fragment.Register_user_fragment_listener,
        Add_user_fragment.Add_user_fragment_listener {

    private Google_map_fragment mGoogleMapFragment;
    private Register_user_fragment mRegisterUserFragment;
    private Add_user_fragment mAddUserFragment;
    private ArrayList<WUser> mUsers = new ArrayList<>();
    private WUser mCurrentUser;
    private int mCurrentUserIndex;//индекс пользователя в массиве юзеров
    private Date mCurrentDate = new Date();

    private boolean mShowDatePicker = false;
    private Button btnDate;

    private Polyline mUserWay;
    private Map<WUser, Marker> mLastCoords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        boolean hasPermissions = PermissionTools.enableMyLocation(this) && PermissionTools.enableReadPhoneState(this);

        mGoogleMapFragment = Google_map_fragment.newInstance();

        btnDate = (Button) findViewById(R.id.btnDate);
        btnDate.setOnClickListener(btnDateListener);
        setViewsVisibility();


        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (hasPermissions) {
            DataLab dataLab = DataLab.get(getApplicationContext());
            mUsers = dataLab.getWUserList();
            mCurrentUser = dataLab.getCurrentUser(mUsers);
            registerOrLoginUser();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    new OtherTools().createDrawerMenu(mUsers, navigationView.getMenu());
                }
            };
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        } else {
            Debug.Log("PERMISSIONS", "NO PERMISSION");
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

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
                mShowDatePicker = false;
                setViewsVisibility();
                mAddUserFragment = Add_user_fragment.newInstance();
                new UITools().displayFragment(this, mAddUserFragment);
                break;
            }
            case R.id.action_update_map: {
                //mLastUserCoordinates = DataLab.get(getApplicationContext()).getLastCoords(mUsers);
                //drawLastCoords();
                break;
            }
     /*       case R.id.action_stop_service: {
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
            */
            case R.id.action_create_online_tables: {
                new DbSchemaOnline().create_online_tables(getApplicationContext());
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == 0) {
            mShowDatePicker = false;
            drawLastCoords();
        } else {
            mCurrentDate = new Date();
            mShowDatePicker = true;
            mCurrentUserIndex = id;
            drawUserWay();
        }
        setViewsVisibility();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //start audio recording or whatever you planned to do
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //Show an explanation to the user *asynchronously*
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("This permission is important to record audio.")
                                .setTitle("Important permission required");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityCompat.requestPermissions(ActivityMain.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                            }
                        });
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                    } else {
                        //Never ask again and handle your app without permission.
                    }
                }
                break;
            }

            case MY_PERMISSION_REQUEST_READ_PHONE_STATE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //start audio recording or whatever you planned to do
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                        //Show an explanation to the user *asynchronously*
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("This permission is important to record audio.")
                                .setTitle("Important permission required");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityCompat.requestPermissions(ActivityMain.this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSION_REQUEST_READ_PHONE_STATE);
                            }
                        });
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSION_REQUEST_READ_PHONE_STATE);
                    } else {
                        //Never ask again and handle your app without permission.
                    }
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void registerOrLoginUser() {
        boolean showRegisterForm = false;
        WUser onlineUser = null;
        if (mCurrentUser != null) {
            onlineUser = OnlineDataLab.get(getApplicationContext()).getOnlineUser(mCurrentUser);
        }
        if (mCurrentUser != null && onlineUser != null) {
            if (mCurrentUser.equals(onlineUser)) {
                new UITools().displayFragment(this, mGoogleMapFragment);
                if (!OtherTools.isServiceRunning(getApplicationContext(), WTrackerServise.class)
                        && mCurrentUser != null) {
                    Intent intent = new Intent(getApplicationContext(), WTrackerServise.class);
                    startService(intent);
                }
            } else {
                showRegisterForm = true;
            }
        } else if (mCurrentUser == null && onlineUser != null) {
            //есть на сервере, нет локально
            showRegisterForm = true;
        } else if (mCurrentUser != null && onlineUser == null) {
            //есть локально, нет онлайн
            showRegisterForm = true;

        } else if (mCurrentUser == null && onlineUser == null) {
            //есть локально, нет онлайн
            showRegisterForm = true;

        }
        if (showRegisterForm) {
            mRegisterUserFragment = Register_user_fragment.newInstance();
            new UITools().displayFragment(this, mRegisterUserFragment);
        }
    }


    @Override
    public void onClickButtonRegisterLoginUser(String buttonType, WUser user) {
        TextView tvInfoMessage = (TextView) mRegisterUserFragment.getView()
                .findViewById(R.id.tvInfoMessage);
        if (buttonType.equals("REGISTER")) {
            if (new OtherTools().registerNewUser(getApplicationContext(), user)) {
                tvInfoMessage.setText("Регистрация успешна!");
                mUsers = DataLab.get(getApplicationContext()).getWUserList();
                mCurrentUser = DataLab.get(getApplicationContext()).getCurrentUser(mUsers);

                new UITools().displayFragment(this, mGoogleMapFragment);
            } else {
                tvInfoMessage.setText("Ошибка регистрации!");
            }
        } else if (buttonType.equals("LOGIN")) {
            if (new OtherTools().loginUser(getApplicationContext(), user)) {
                mUsers = DataLab.get(getApplicationContext()).getWUserList();
                mCurrentUser = DataLab.get(getApplicationContext()).getCurrentUser(mUsers);

                new UITools().displayFragment(this, mGoogleMapFragment);

            }

        }
    }

    @Override
    public void onClickButtonAddUser(WUser user) {
        TextView tvInfoMessage = (TextView) mAddUserFragment.getView().findViewById(R.id.tvInfoMessage);
        if (new OtherTools().addUser(getApplicationContext(), user)) {
            tvInfoMessage.setText("Пользователь добавлен успешно!");
            mUsers = DataLab.get(getApplicationContext()).getWUserList();
            mCurrentUser = DataLab.get(getApplicationContext()).getCurrentUser(mUsers);

            new UITools().displayFragment(this, mGoogleMapFragment);
        } else {
            tvInfoMessage.setText("Ошибка добавления!");
        }

    }

    private void setViewsVisibility() {
        if (mShowDatePicker) {
            btnDate.setVisibility(View.VISIBLE);
            btnDate.setText(new DateFormatTools().dateToString(mCurrentDate, DateFormatTools.DATE_FORMAT_VID));
        } else {
            btnDate.setVisibility(View.INVISIBLE);
        }
    }

    private View.OnClickListener btnDateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // определяем текущую дату
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // создаем DatePickerDialog и возвращаем его
            Dialog picker = new DatePickerDialog(ActivityMain.this, onDateSetListener,
                    year, month, day);
            picker.setTitle("Дата маршрута");
            picker.show();
        }
    };
    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            mCurrentDate = new DateFormatTools().getDate(i, i1, i2);
            setViewsVisibility();
            drawUserWay();
        }
    };

    private void drawUserWay() {
        UITools uiTools = new UITools();
        uiTools.displayFragment(ActivityMain.this, mGoogleMapFragment);
        if (mUserWay != null) mUserWay.remove();
        mUserWay = uiTools.drawUserWay(getApplicationContext(), mGoogleMapFragment.mMap, mUsers.get(mCurrentUserIndex - 1), mCurrentDate);

    }

    private void drawLastCoords() {
        UITools uiTools = new UITools();
        uiTools.displayFragment(ActivityMain.this, mGoogleMapFragment);
        if (mLastCoords != null) {
            if (mLastCoords.size() > 0) {
                for (Map.Entry<WUser, Marker> entry : mLastCoords.entrySet()) {
                    entry.getValue().remove();
                }
            }
        }
        mLastCoords = uiTools.drawLastCoords(mGoogleMapFragment.mMap, DataLab.get(getApplicationContext()).getLastCoords(mUsers));

    }
}