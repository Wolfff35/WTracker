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
import com.wolff.wtracker.ActivityMain;

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
    public static boolean enableWriteExternalStorage(final AppCompatActivity activity) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);

        }else {
            flag=true;
        }
        return flag;
    }

    public static void onRequestPermissionResult(final Activity activity, int requestCode,
                                          String permissions[], int[] grantResults){

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                 checkPermissionResult(activity,grantResults[0],Manifest.permission.ACCESS_FINE_LOCATION,MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                break;
            }

            case MY_PERMISSION_REQUEST_READ_PHONE_STATE: {
                checkPermissionResult(activity,grantResults[0],Manifest.permission.READ_PHONE_STATE,MY_PERMISSION_REQUEST_READ_PHONE_STATE);

                break;
            }
            case MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE: {
                checkPermissionResult(activity,grantResults[0],Manifest.permission.WRITE_EXTERNAL_STORAGE,MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);

                break;
            }
        }

    }
    private static void checkPermissionResult(final Activity activity, int grantResult0, final String permission,final int requestCode){
        if (grantResult0 == PackageManager.PERMISSION_GRANTED) {
            //start audio recording or whatever you planned to do
        } else if (grantResult0 == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                //Show an explanation to the user *asynchronously*
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
                builder.setMessage("This permission is important to record audio.")
                        .setTitle("Important permission required");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                    }
                });
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            } else {
                //Never ask again and handle your app without permission.
            }
        }

    }
}