package com.wolff.wtracker.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import com.wolff.wtracker.R;
import com.wolff.wtracker.localdb.DataLab;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.online.AsyncInsertUser;
import com.wolff.wtracker.online.OnlineDataLab;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by wolff on 24.09.2017.
 */

public class OtherTools {
    private static final String LOG_TAG = "OtherTools";

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //=====
    public String getIMEI(Context context) {
        if(!PermissionTools.enableReadPhoneState((AppCompatActivity) context)){
            return null;
        }
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getDeviceId();
    }
    public boolean addUser(Context context, WUser newUser) {
            WUser onlineUser = OnlineDataLab.get(context).getOnlineUser(newUser);
            if (onlineUser != null) {
                WUser localUser = DataLab.get(context).getUserById(onlineUser.get_id_user(),DataLab.get(context).getWUserList());
                if(localUser==null) {
                    if (onlineUser.get_id_user().equals(newUser.get_id_user()) &&
                            onlineUser.get_pin_for_access().equals(newUser.get_pin_for_access())
                            &&onlineUser.get_imei_phone().equals(newUser.get_imei_phone())) {
                        DataLab.get(context).user_add(newUser);
                        return true;
                    } else {
                        return false;
                    }
                }else {
                    return false;
                }
            } else {
                return false;
            }

    }

    public boolean registerNewUser(Context context, WUser user) {
        if(user==null){
            return false;
        }
        try {
            WUser existUser = OnlineDataLab.get(context).getOnlineUser(user);

            if (existUser == null) {
                //register
                AsyncInsertUser taskInsertUser = new AsyncInsertUser(context, user);
                boolean isSuccess = taskInsertUser.execute().get();
                if (isSuccess) {
                    DataLab.get(context).user_add(user);
                    return true;
                } else {
                    return false;
                }
            } else {
                Debug.Log(LOG_TAG, " Пользователь с таким номером уже существует!");
                return false;
            }
        } catch (InterruptedException e) {
            Debug.Log(LOG_TAG, " ERROR 1 " + e.getLocalizedMessage());
            return false;
        } catch (ExecutionException e) {
            Debug.Log(LOG_TAG, " ERROR 2 " + e.getLocalizedMessage());
            return false;
        }
    }

    public boolean loginUser(Context context, WUser user) {
        if(user==null){
            return false;
        }
        WUser localUser = DataLab.get(context).queryWUserById(user.get_id_user());
        if(localUser==null) {
            DataLab.get(context).user_add(user);
        }
            WUser existUser = OnlineDataLab.get(context).getOnlineUser(user);

            if (existUser == null) {
                Debug.Log(LOG_TAG, "Нет такого пользователя!");
                return false;
            } else {
                return existUser.get_imei_phone().equals(user.get_imei_phone()) &&
                        existUser.get_password().equals(user.get_password());
            }
     }

    public void createDrawerMenu(ArrayList<WUser> userList, Menu menu) {
          menu.clear();
        menu.add(Menu.NONE, 0, Menu.NONE, "Последние координаты");
        int i = 1;
        //SubMenu group = menu.addSubMenu("-");

        for (WUser user : userList) {
            menu.add(R.id.menu_group, i, Menu.NONE, user.get_name() + "(" + user.get_id_user() + ")");
            i++;
        }

    }
    public void fillDrawerHeader(View view,WUser user){
        TextView tvCurrentUserName = (TextView)view.findViewById(R.id.tvCurrentUserName);
        tvCurrentUserName.setText(user.get_name());

        TextView tvCurrentUserPhone = (TextView)view.findViewById(R.id.tvCurrentUserPhone);
        tvCurrentUserPhone.setText(user.get_phone().substring(0,2)
                +"("+user.get_phone().substring(2,5)+") "
                +user.get_phone().substring(5,8)+"-"
                +user.get_phone().substring(8,10)+"-"
                +user.get_phone().substring(10,12));
        TextView tvCurrentUserIMEI = (TextView)view.findViewById(R.id.tvCurrentUserIMEI);
        //tvCurrentUserIMEI.setText(user.get_imei_phone());
        tvCurrentUserIMEI.setText(user.get_imei_phone().substring(0,3)+"-"
                +user.get_imei_phone().substring(3,6)+"-"
                +user.get_imei_phone().substring(6,9)+"-"
                +user.get_imei_phone().substring(9,12)+"-"
                +user.get_imei_phone().substring(12,15));


    }
}
