package com.wolff.wtracker.online;

import android.content.Context;
import android.os.AsyncTask;

import com.wolff.wtracker.localdb.DataLab;
import com.wolff.wtracker.localdb.DbSchema;
import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.tools.Debug;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.wolff.wtracker.online.DbSchemaOnline.MSSQL_DB;
import static com.wolff.wtracker.online.DbSchemaOnline.MSSQL_LOGIN;
import static com.wolff.wtracker.online.DbSchemaOnline.MSSQL_PASS;


/**
 * Created by wolff on 25.09.2017.
 */
public final class AsyncInsertCoords extends AsyncTask<String, Void, Boolean> {
    private Context mContext;
    private WUser mCurrentUser;
    private ArrayList<WCoord> mCoords;

    private static final String REMOTE_TABLE = "[tessst_gps].[dbo].[t_coords]";
    private static final String SQL = "INSERT into " + REMOTE_TABLE + " (" + DbSchema.Table_Users.Cols.ID_USER +
            "," + DbSchema.Table_Users.Cols.IMEI_PHONE +
            "," + DbSchema.Table_Coords.Cols.DATE +
            "," + DbSchema.Table_Coords.Cols.TIME +
            "," + DbSchema.Table_Coords.Cols.COORD_PROVIDER +
            "," + DbSchema.Table_Coords.Cols.COORD_LAT +
            "," + DbSchema.Table_Coords.Cols.COORD_LON +
            "," + DbSchema.Table_Coords.Cols.COORD_ACCURACY +
            "," + DbSchema.Table_Coords.Cols.COORD_ALTITUDE +
            "," + DbSchema.Table_Coords.Cols.COORD_BEARING +
            ") values(?,?,?,?,?,?,?,?,?,?)";


    public AsyncInsertCoords(Context context, WUser currentUser, ArrayList<WCoord> data) {
        this.mContext = context;
        this.mCoords = data;
        this.mCurrentUser = currentUser;
    }

    @Override
    protected Boolean doInBackground(String... proc_params) {
        OnlineDataLab onlineDataLab = OnlineDataLab.get(mContext);
        Connection con = onlineDataLab.getOnlineConnection(MSSQL_DB, MSSQL_LOGIN, MSSQL_PASS);
        if (con != null) {
            PreparedStatement prepared = null;
            try {
                prepared = con.prepareStatement(SQL);
                for (WCoord coord : mCoords) {
                    prepared.setString(1, mCurrentUser.get_id_user());
                    prepared.setString(2, mCurrentUser.get_imei_phone());
                    prepared.setTimestamp(3, new java.sql.Timestamp(coord.get_date().getTime()));
                    prepared.setTime(4, new java.sql.Time(coord.get_date().getTime()));
                    prepared.setString(5, coord.get_provider());
                    prepared.setDouble(6, coord.get_coord_lat());
                    prepared.setDouble(7, coord.get_coord_lon());
                    prepared.setDouble(8, coord.get_accuracy());
                    prepared.setDouble(9, coord.get_altitude());
                    prepared.setDouble(10, coord.get_bearing());
                    prepared.addBatch();
                }
                prepared.executeBatch();
                return true;
            } catch (SQLException e) {
                Debug.Log("AsyncInsertCoords", "ERROR 4 " + e.getLocalizedMessage());
            } finally {
                try {
                    if (prepared != null) prepared.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                    //throw new RuntimeException(e.getMessage());
                    Debug.Log("AsyncInsertCoords", "ERROR 5 " + e.getLocalizedMessage());
                }
            }
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean){

            for (WCoord coord:mCoords){
                DataLab.get(mContext).coord_delete(coord);
            }
        }
    }
}