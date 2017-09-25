package com.wolff.wtracker.online;

import android.content.Context;

import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.model.WUser;

/**
 * Created by wolff on 25.09.2017.
 */

public class OnlineDataLab {
    private static OnlineDataLab sDataLab;

    private Context mContext;

    private OnlineDataLab(Context context){
        mContext = context.getApplicationContext();
    }
    public static OnlineDataLab get(Context context){
        if(sDataLab==null){
            sDataLab = new OnlineDataLab(context);
        }
        return sDataLab;
    }
//==================================================================================================

    public WCoord getLastCoordinates(WUser user){
        return null;
    }
}
