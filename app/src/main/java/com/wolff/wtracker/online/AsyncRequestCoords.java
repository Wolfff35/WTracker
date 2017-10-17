package com.wolff.wtracker.online;

import android.content.Context;
import android.os.AsyncTask;

import com.wolff.wtracker.localdb.DbSchema;
import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.tools.DateFormatTools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import static com.wolff.wtracker.online.DbSchemaOnline.MSSQL_DB;
import static com.wolff.wtracker.online.DbSchemaOnline.MSSQL_LOGIN;
import static com.wolff.wtracker.online.DbSchemaOnline.MSSQL_PASS;

/**
 * Created by wolff on 02.10.2017.
 */

public final class AsyncRequestCoords extends AsyncTask<Void, Void, ArrayList<WCoord>> {

    private Context mContext;
    private WUser mCurrentUser;
    private Date mCurrentDate;
    private static final String LOG_TAG = "AsyncRequestCoords";
    private static final String REMOTE_TABLE = "[tessst_gps].[dbo].[t_coords]";


    public AsyncRequestCoords(Context context, WUser currentUser,Date currentDate) {
        this.mContext = context;
        this.mCurrentUser = currentUser;
        this.mCurrentDate = currentDate;
    }

    @Override
    protected ArrayList<WCoord> doInBackground(Void... query) {
        ArrayList<WCoord> coordList = new ArrayList<>();
        DateFormatTools dft = new DateFormatTools();
        String SQL = "SELECT * FROM " + REMOTE_TABLE + " WHERE " + DbSchema.Table_Users.Cols.ID_USER + " = " + mCurrentUser.get_id_user()+
                "AND "+DbSchema.Table_Coords.Cols.DATE+" = "+ dft.dateToString(mCurrentDate,DateFormatTools.DATE_FORMAT_SHORT);

        Connection con = OnlineDataLab.get(mContext).getOnlineConnection(MSSQL_DB, MSSQL_LOGIN, MSSQL_PASS);
            Statement st = null;
            ResultSet rs = null;
            try {
                if (con != null) {
                    st = con.createStatement();
                    rs = st.executeQuery(SQL);
                    if (rs != null) {
                        while (rs.next()) {
                            WCoord coord = new WCoord();
                            coord.set_date(rs.getDate(DbSchema.Table_Coords.Cols.DATE));
                            coord.set_id(rs.getDouble(DbSchema.Table_Coords.Cols.ID));
                            coord.set_provider(rs.getString(DbSchema.Table_Coords.Cols.COORD_PROVIDER));
                            coord.set_coord_lon(rs.getDouble(DbSchema.Table_Coords.Cols.COORD_LON));
                            coord.set_coord_lat(rs.getDouble(DbSchema.Table_Coords.Cols.COORD_LAT));
                            coord.set_accuracy(rs.getDouble(DbSchema.Table_Coords.Cols.COORD_ACCURACY));
                            coord.set_altitude(rs.getDouble(DbSchema.Table_Coords.Cols.COORD_ALTITUDE));
                            coord.set_bearing(rs.getDouble(DbSchema.Table_Coords.Cols.COORD_BEARING));
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
