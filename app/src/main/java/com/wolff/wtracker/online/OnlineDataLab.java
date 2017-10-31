package com.wolff.wtracker.online;

import android.content.Context;

import com.wolff.wtracker.localdb.DbSchema;
import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.tools.DateFormatTools;
import com.wolff.wtracker.tools.Debug;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by wolff on 25.09.2017.
 */

public class OnlineDataLab {
    private static final String LOG_TAG = "OnlineDataLab";
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
    public WUser getOnlineUser(WUser currentUser) {
        String REMOTE_TABLE = "[tessst_gps].[dbo].[t_users]";
        String SQL = "SELECT * FROM " + REMOTE_TABLE + " WHERE " + DbSchema.Table_Users.Cols.ID_USER + " = " + currentUser.get_id_user();
        AsyncRequest task = new AsyncRequest(mContext);

        try {
            List rs = task.execute(SQL).get();
            if(rs.size()==1){
                WUser user = new WUser();
                HashMap<String,Object> onlUser = (HashMap) rs.get(0);
                user.set_id_user((String) onlUser.get(DbSchema.Table_Users.Cols.ID_USER));
                user.set_imei_phone((String) onlUser.get(DbSchema.Table_Users.Cols.IMEI_PHONE));
                user.set_password((String) onlUser.get(DbSchema.Table_Users.Cols.PASSWORD));
                user.set_pin_for_access((String) onlUser.get(DbSchema.Table_Users.Cols.PIN_FOR_ACCESS));
                user.set_name((String) onlUser.get(DbSchema.Table_Users.Cols.NAME));
                user.set_phone((String) onlUser.get(DbSchema.Table_Users.Cols.ID_USER));
                return user;
            }else {
                Debug.Log(LOG_TAG,"getOnlineUser - WRONG SELECTION");
            }
        } catch (InterruptedException e) {
            Debug.Log(LOG_TAG, "ERROR 1 " + e.getLocalizedMessage());
        } catch (ExecutionException e) {
            Debug.Log(LOG_TAG, "ERROR 2 " + e.getLocalizedMessage());
        }
        return null;
    }

    public ArrayList<WCoord> getOnlineUsersCoordinates(WUser currentUser,Date currentDate) {
        String REMOTE_TABLE = "[tessst_gps].[dbo].[t_coords]";
        DateFormatTools dft = new DateFormatTools();
        String SQL = "SELECT * FROM " + REMOTE_TABLE + " WHERE " + DbSchema.Table_Users.Cols.ID_USER + " = " + currentUser.get_id_user()+
                " AND "+DbSchema.Table_Coords.Cols.DATE+" = '"+ dft.dateToString(currentDate, DateFormatTools.DATE_FORMAT_SHORT)+
                "' ORDER BY "+DbSchema.Table_Coords.Cols.DATE+" DESC";
        AsyncRequest task = new AsyncRequest(mContext);
        ArrayList<WCoord> coordList = new ArrayList<>();

        try {
            List rs = task.execute(SQL).get();
            for(int i=0;i<rs.size();i++){
                WCoord coord = new WCoord();
                HashMap onlCoord = (HashMap) rs.get(i);
                Date t =  dft.dateFromString((String)onlCoord.get(DbSchema.Table_Coords.Cols.TIME),DateFormatTools.TIME_FORMAT_SQL_LONG);
                coord.set_date(t);
                coord.set_provider((String) onlCoord.get(DbSchema.Table_Coords.Cols.COORD_PROVIDER));
                coord.set_coord_lon((Double) onlCoord.get(DbSchema.Table_Coords.Cols.COORD_LON));
                coord.set_coord_lat((Double) onlCoord.get(DbSchema.Table_Coords.Cols.COORD_LAT));
                coord.set_accuracy((Double) onlCoord.get(DbSchema.Table_Coords.Cols.COORD_ACCURACY));
                coord.set_altitude((Double) onlCoord.get(DbSchema.Table_Coords.Cols.COORD_ALTITUDE));
                coord.set_bearing((Double) onlCoord.get(DbSchema.Table_Coords.Cols.COORD_BEARING));
                coordList.add(coord);
            }
            Collections.sort(coordList, new Comparator<WCoord>() {
                @Override
                public int compare(WCoord o1, WCoord o2) {
                    return o1.get_date().compareTo(o2.get_date());
                }
            });
            return coordList;
         } catch (InterruptedException e) {
            Debug.Log(LOG_TAG, "ERROR 1 " + e.getLocalizedMessage());
        } catch (ExecutionException e) {
            Debug.Log(LOG_TAG, "ERROR 2 " + e.getLocalizedMessage());
        }
        return null;
    }

    //==============================================================================================
/*    public void writeCoordsToServer(WUser user, ArrayList<WCoord>coords){
         AsyncInsertCoords task = new AsyncInsertCoords(mContext,user,coords);
            task.execute();
    }
*/

}
