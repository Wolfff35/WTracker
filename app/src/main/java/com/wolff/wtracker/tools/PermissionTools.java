package com.wolff.wtracker.tools;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by wolff on 03.10.2017.
 */

public abstract class PermissionTools {
    public static final int MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final int MY_PERMISSION_REQUEST_READ_PHONE_STATE = 2;
    public static final int MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 3;

    public static boolean enableReadPhoneState(final AppCompatActivity activity) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSION_REQUEST_READ_PHONE_STATE);

        }else {
            flag=true;
        }
        return flag;
    }
    public static boolean enableMyLocation(final AppCompatActivity activity) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);

        }else {
            flag=true;
        }
        return flag;
    }

}