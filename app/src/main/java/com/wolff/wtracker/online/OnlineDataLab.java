package com.wolff.wtracker.online;

import android.content.Context;

import com.wolff.wtracker.localdb.DataLab;
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
import java.util.Map;
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
            if(rs==null) return null;
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
        Debug.start("getOnlineUsersCoordinates");
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
                //Date t =  dft.dateFromString((String)onlCoord.get(DbSchema.Table_Coords.Cols.TIME),DateFormatTools.TIME_FORMAT_SQL_LONG);
                Date t =  dft.dateFromString((String)onlCoord.get(DbSchema.Table_Coords.Cols.DATE)+" "+(String)onlCoord.get(DbSchema.Table_Coords.Cols.TIME),DateFormatTools.DATE_FORMAT_SQL_LONG);
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
            Debug.Log("Coords","Count = "+coordList.size());
            Debug.stop();
            return coordList;
         } catch (InterruptedException e) {
            Debug.Log(LOG_TAG, "ERROR 1 " + e.getLocalizedMessage());
        } catch (ExecutionException e) {
            Debug.Log(LOG_TAG, "ERROR 2 " + e.getLocalizedMessage());
        }
        return null;
    }
    public Map<WUser, WCoord> getOnlineLastCoords(ArrayList<WUser> users) {
        String filter = "";
        for (WUser u:users) {
            filter = filter+"'"+u.get_id_user()+"',";
        }
        filter = filter.substring(0,filter.length()-1);
        DateFormatTools dft = new DateFormatTools();
        String SQL ="SELECT table_max.uuser AS _id_user," +
                "table_max.pphone AS _id_phone,table_max.ddate AS _date,table_max.ttime AS _time," +
                "table_coord._coord_lat,table_coord._coord_lon,table_coord._provider," +
                "table_coord._altitude,table_coord._accuracy,table_coord._bearing " +
                "FROM (SELECT tab_date.id_user AS uuser,tab_date.id_phone AS pphone," +
                "tab_date.max_date AS ddate,tab_time.max_time AS ttime " +
                "FROM (SELECT _id_user AS id_user,_id_phone AS id_phone,MAX(_date) AS max_date " +
                "FROM [tessst_gps].[dbo].[t_coords] " +
                "WHERE _id_user IN ("+filter+") " +
                //"WHERE _id_user IN ('380996649531','380673231646') " +
                "GROUP BY _id_user,_id_phone) tab_date, " +
                "(SELECT _id_user,_id_phone,_date,MAX(_time) AS max_time " +
                "FROM [tessst_gps].[dbo].[t_coords] GROUP BY _id_user,_id_phone,_date) tab_time " +
                "WHERE tab_date.id_user=tab_time._id_user AND tab_date.id_phone=tab_time._id_phone " +
                "AND tab_date.max_date=tab_time._date) table_max, " +
                "(SELECT _id_user,_id_phone,_date,_time,_coord_lat,_coord_lon,_provider,_altitude,_accuracy,_bearing " +
                "FROM [tessst_gps].[dbo].[t_coords] " +
                "GROUP BY _id_user,_id_phone,_date,_time,_coord_lat,_coord_lon,_provider,_altitude,_accuracy,_bearing) table_coord " +
                "WHERE table_max.uuser=table_coord._id_user AND table_max.pphone = table_coord._id_phone " +
                "AND table_max.ddate=table_coord._date AND table_max.ttime = table_coord._time";
        AsyncRequest task = new AsyncRequest(mContext);
        Map<WUser, WCoord> coordList = new HashMap<>();

        try {
            List rs = task.execute(SQL).get();
            if(rs==null) return null;
            for(int i=0;i<rs.size();i++){
                WCoord coord = new WCoord();
                HashMap onlCoord = (HashMap) rs.get(i);
                String user_id = (String)onlCoord.get(DbSchema.Table_Users.Cols.ID_USER);
                WUser user = DataLab.get(mContext).getUserById(user_id,users);
                //if(user!=null) {
                user.set_imei_phone((String) onlCoord.get(DbSchema.Table_Users.Cols.IMEI_PHONE));
               // }
                //Date t =  dft.dateFromString((String)onlCoord.get(DbSchema.Table_Coords.Cols.TIME),DateFormatTools.TIME_FORMAT_SQL_LONG);
                Date t =  dft.dateFromString((String)onlCoord.get(DbSchema.Table_Coords.Cols.DATE)+" "+(String)onlCoord.get(DbSchema.Table_Coords.Cols.TIME),DateFormatTools.DATE_FORMAT_SQL_LONG);
                coord.set_date(t);
                coord.set_provider((String) onlCoord.get(DbSchema.Table_Coords.Cols.COORD_PROVIDER));
                coord.set_coord_lon((Double) onlCoord.get(DbSchema.Table_Coords.Cols.COORD_LON));
                coord.set_coord_lat((Double) onlCoord.get(DbSchema.Table_Coords.Cols.COORD_LAT));
                coord.set_accuracy((Double) onlCoord.get(DbSchema.Table_Coords.Cols.COORD_ACCURACY));
                coord.set_altitude((Double) onlCoord.get(DbSchema.Table_Coords.Cols.COORD_ALTITUDE));
                coord.set_bearing((Double) onlCoord.get(DbSchema.Table_Coords.Cols.COORD_BEARING));
                coordList.put(user,coord);
            }
            Debug.Log("Coords","Count = "+coordList.size());
            Debug.stop();
            return coordList;
        } catch (InterruptedException e) {
            Debug.Log(LOG_TAG, "ERROR 1 " + e.getLocalizedMessage());
        } catch (ExecutionException e) {
            Debug.Log(LOG_TAG, "ERROR 2 " + e.getLocalizedMessage());
        }
        return null;
    }
    public WCoord getOnlineUserLastCoord(WUser user) {
        WCoord last = null;
       ArrayList<WUser> users = DataLab.get(mContext).getWUserList();
        Map<WUser, WCoord> lastCoord = getOnlineLastCoords(users);
         for (Map.Entry<WUser, WCoord> entry : lastCoord.entrySet()) {
            if (entry.getKey().get_id_user().equalsIgnoreCase(user.get_id_user())) {
                last = entry.getValue();
            }
        }
        if (last != null) {
        } else {
            Debug.Log("getUserLastCoord", "USER = " + user.get_id_user() + " - NO LAST COORDINATES!!");
        }

        return last;

    }

}
