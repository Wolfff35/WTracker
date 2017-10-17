package com.wolff.wtracker.online;

import android.content.Context;

import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.tools.Debug;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by wolff on 25.09.2017.
 */

public class OnlineDataLab {
    private static OnlineDataLab sDataLab;

    private Context mContext;

    private OnlineDataLab(Context context) {
        mContext = context;
    }

    public static OnlineDataLab get(Context context) {
        if (sDataLab == null) {
            sDataLab = new OnlineDataLab(context);
        }
        return sDataLab;
    }

    //==================================================================================================
    public Connection getOnlineConnection(String databaseName, String login, String pass) {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            Connection con = null;
            try {
                con = DriverManager.getConnection(databaseName, login, pass);
                return con;
            } catch (SQLException e) {
                Debug.Log("getOnlineConnection","ERROR 1 "+e.getLocalizedMessage());
            } finally {
            }
        } catch (ClassNotFoundException e) {
            Debug.Log("getOnlineConnection","ERROR 2 "+e.getLocalizedMessage());
        }
        return null;
    }

    //==============================================================================================
    public void writeCoordsToServer(WUser user, ArrayList<WCoord>coords){
         AsyncInsertCoords task = new AsyncInsertCoords(mContext,user,coords);
            task.execute();
    }

    public WCoord getLastCoordinates(WUser user) {
        return null;
    }
}
