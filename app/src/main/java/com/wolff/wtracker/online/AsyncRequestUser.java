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

import static android.R.attr.data;
import static com.wolff.wtracker.online.AsyncRequest.MSSQL_DB;
import static com.wolff.wtracker.online.AsyncRequest.MSSQL_LOGIN;
import static com.wolff.wtracker.online.AsyncRequest.MSSQL_PASS;

/**
 * Created by wolff on 02.10.2017.
 */

public final class AsyncRequestUser extends AsyncTask<String, Void, WUser> {
    private Context mContext;
    private WUser mCurrentUser;
    private static final String REMOTE_TABLE = "[tessst_gps].[dbo].[t_users]";
    public AsyncRequestUser(Context context,WUser currentUser) {
        this.mContext = context;
        this.mCurrentUser = currentUser;
    }

    @Override
    protected WUser doInBackground(String... query) {
        String SQL = "SELECT * FROM " + REMOTE_TABLE + " WHERE " + DbSchema.Table_Users.Cols.ID_USER + " = " + mCurrentUser.get_id_user();
        Connection con = OnlineDataLab.get(mContext).getOnlineConnection(MSSQL_DB, MSSQL_LOGIN, MSSQL_PASS);
        Statement st = null;
        ResultSet rs = null;
        if (con != null) {
            try {
                st = con.createStatement();
                rs = st.executeQuery(SQL);
                if (rs != null) {
                    int columnCount = rs.getMetaData().getColumnCount();
                    while (rs.next()) {
                        //JSONObject rowObject = new JSONObject();
                        for (int i = 1; i <= columnCount; i++) {
                           // rowObject.put(rs.getMetaData().getColumnName(i), (rs.getString(i) != null) ? rs.getString(i) : "");
                        }
                        //                          resultSet.put(rowObject);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (rs != null) rs.close();
                    if (st != null) st.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }


        }

        //    return resultSet;
        return null;
    }

    @Override
    protected void onPostExecute(WUser result) {

    }
}
