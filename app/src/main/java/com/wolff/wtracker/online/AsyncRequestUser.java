package com.wolff.wtracker.online;

import android.content.Context;
import android.os.AsyncTask;

import com.wolff.wtracker.localdb.DbSchema;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.tools.Debug;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.wolff.wtracker.online.DbSchemaOnline.MSSQL_DB;
import static com.wolff.wtracker.online.DbSchemaOnline.MSSQL_LOGIN;
import static com.wolff.wtracker.online.DbSchemaOnline.MSSQL_PASS;

/**
 * Created by wolff on 02.10.2017.
 */

public final class AsyncRequestUser extends AsyncTask<String, Void, WUser> {
    private Context mContext;
    private WUser mCurrentUser;
    private static final String LOG_TAG = "AsyncRequestUser";
    private static final String REMOTE_TABLE = "[tessst_gps].[dbo].[t_users]";

    public AsyncRequestUser(Context context, WUser currentUser) {
        this.mContext = context;
        this.mCurrentUser = currentUser;
    }

    @Override
    protected WUser doInBackground(String... query) {
        Debug.Log(LOG_TAG, "Begin");

        String SQL = "SELECT * FROM " + REMOTE_TABLE + " WHERE " + DbSchema.Table_Users.Cols.ID_USER + " = " + mCurrentUser.get_id_user();
        //String SQL = "SELECT * FROM " + REMOTE_TABLE + " WHERE " + DbSchema.Table_Users.Cols.ID_USER + " = 380996649531";// + mCurrentUser.get_id_user();
        Connection con = OnlineDataLab.get(mContext).getOnlineConnection(MSSQL_DB, MSSQL_LOGIN, MSSQL_PASS);
        WUser user = null;
        Statement st = null;
        ResultSet rs = null;
        if (con != null) {
            Debug.Log(LOG_TAG, "Get connection");
            try {
                st = con.createStatement();
                rs = st.executeQuery(SQL);
                if (rs != null) {
                    if (rs.next()) {
                        user = new WUser();
                        user.set_id_user(rs.getString(DbSchema.Table_Users.Cols.ID_USER));
                        user.set_imei_phone(rs.getString(DbSchema.Table_Users.Cols.IMEI_PHONE));
                        user.set_password(rs.getString(DbSchema.Table_Users.Cols.PASSWORD));
                        user.set_pin_for_access(rs.getString(DbSchema.Table_Users.Cols.PIN_FOR_ACCESS));
                        user.set_name(rs.getString(DbSchema.Table_Users.Cols.NAME));
                        user.set_phone(rs.getString(DbSchema.Table_Users.Cols.ID_USER));
                        return user;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } catch (SQLException e) {
                Debug.Log(LOG_TAG, "ERROR 1 " + e.getLocalizedMessage());
                return null;
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (st != null) st.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }


        } else {
            return null;
        }
        //return user;
    }

    @Override
    protected void onPostExecute(WUser result) {

    }
}
