package com.wolff.wtracker.online;

import android.content.Context;
import android.os.AsyncTask;

import com.wolff.wtracker.localdb.DbSchema;
import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.model.WUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by wolff on 02.10.2017.
 */

public final class AsyncRequestCoords extends AsyncTask<String, Void, ArrayList<WCoord>> {
    final static String MSSQL_DB = "jdbc:jtds:sqlserver://13.10.12.10:1433:/tessst_gps;";
    final static String MSSQL_LOGIN = "sa";
    final static String MSSQL_PASS= "Rfcf,kfyrf";

    private Context mContext;
    private WUser mCurrentUser;
    private static final String LOG_TAG = "AsyncRequestCoords";
    private static final String REMOTE_TABLE = "[tessst_gps].[dbo].[t_coords]";


    public AsyncRequestCoords(Context context, WUser currentUser) {
        this.mContext = context;
        this.mCurrentUser = currentUser;
    }

    @Override
    protected ArrayList<WCoord> doInBackground(String... query) {
        ArrayList<WCoord> coordList = new ArrayList<>();
        String SQL = "SELECT * FROM " + REMOTE_TABLE + " WHERE " + DbSchema.Table_Users.Cols.ID_USER + " = " + mCurrentUser.get_id_user();

        Connection con = OnlineDataLab.get(mContext).getOnlineConnection(MSSQL_DB, MSSQL_LOGIN, MSSQL_PASS);
            Statement st = null;
            ResultSet rs = null;
            try {
                if (con != null) {
                    st = con.createStatement();
                    rs = st.executeQuery(query[0]);
                    if (rs != null) {
                        // Сохранение данных в JSONArray
                        while (rs.next()) {
                            WCoord coord = new WCoord();
                            coord.set_date(rs.getDate(DbSchema.Table_Coords.Cols.DATE));
                            coord.set_date(rs.getDate(DbSchema.Table_Coords.Cols.ID));
                            coord.set_date(rs.getDate(DbSchema.Table_Coords.Cols.COORD_PROVIDER));
                            coord.set_date(rs.getDate(DbSchema.Table_Coords.Cols.COORD_LON));
                            coord.set_date(rs.getDate(DbSchema.Table_Coords.Cols.COORD_LAT));
                            coord.set_date(rs.getDate(DbSchema.Table_Coords.Cols.COORD_ACCURACY));
                            coord.set_date(rs.getDate(DbSchema.Table_Coords.Cols.COORD_ALTITUDE));
                            coord.set_date(rs.getDate(DbSchema.Table_Coords.Cols.COORD_BEARING));
                            coordList.add(coord);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (st != null) st.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        return coordList;
    }

    @Override
    protected void onPostExecute(ArrayList<WCoord> result) {

    }
}
