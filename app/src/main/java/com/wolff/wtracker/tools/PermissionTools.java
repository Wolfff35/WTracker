package com.wolff.wtracker.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.wolff.wtracker.R;

/**
 * Created by wolff on 27.09.2017.
 */

public class PermissionTools {
    public static final int PERMISSION_REQUEST_CODE = 123;
    private String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
    public boolean hasPermissions(Context context){
        int res = 0;
        for (String perms : permissions){
            res = context.checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    private void requestPerms(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            activity.requestPermissions(permissions,PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionWithRationale(final Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            final String message = "Storage permission is needed to show files count";
            //Snackbar.make(activity.getClass().findViewById(R.id.activity_view), message, Snackbar.LENGTH_LONG)
            Snackbar.make(activity.findViewById(R.id.item_container), message, Snackbar.LENGTH_LONG)
                    .setAction("GRANT", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPerms(activity);
                        }
                    })
                    .show();
        } else {
            requestPerms(activity);
        }
    }
    public void showPermissionSnackbar(final Activity activity) {
        Snackbar.make(activity.findViewById(R.id.item_container), "Permission isn't granted" , Snackbar.LENGTH_LONG)
                .setAction("SETTINGS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApplicationSettings(activity);

                        Toast.makeText(activity,
                                "Open Permissions and grant the Storage permission",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .show();
    }
    private void openApplicationSettings(Activity activity) {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE);
    }

}
