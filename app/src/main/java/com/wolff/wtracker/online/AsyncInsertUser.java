package com.wolff.wtracker.online;

import android.content.Context;
import android.os.AsyncTask;

import com.wolff.wtracker.localdb.DbSchema;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.tools.Debug;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.wolff.wtracker.online.DbSchemaOnline.MSSQL_DB;
import static com.wolff.wtracker.online.DbSchemaOnline.MSSQL_LOGIN;
import static com.wolff.wtracker.online.DbSchemaOnline.MSSQL_PASS;


/**
 * Created by wolff on 25.09.2017.
 */
public final class AsyncInsertUser extends AsyncTask<String, Void, Boolean> {
    private Context mContext;
    private WUser mCurrentUser;

    private static final String LOG_TAG = "AsyncInsertUser";

    private static final String REMOTE_TABLE = "[tessst_gps].[dbo].[t_users]";
    private static final String SQL = "INSERT into "+REMOTE_TABLE+
                                        " ("+
                                        DbSchema.Table_Users.Cols.ID_USER+
                                        ","+DbSchema.Table_Users.Cols.IMEI_PHONE+
                                        ","+DbSchema.Table_Users.Cols.PASSWORD+
                                        ","+DbSchema.Table_Users.Cols.PIN_FOR_ACCESS+
                                        ") values(?,?,?,?)";

     public AsyncInsertUser(Context context, WUser currentUser) {
        this.mContext = context;
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
                prepared.setString(1, mCurrentUser.get_id_user());
                prepared.setString(2, mCurrentUser.get_imei_phone());
                prepared.setString(3, mCurrentUser.get_password());
                prepared.setString(4, mCurrentUser.get_pin_for_access());
                prepared.addBatch();
                prepared.executeBatch();
                return true;
            } catch (SQLException e) {
                Debug.Log(LOG_TAG, "ERROR 4 " + e.getLocalizedMessage());

            } finally {
                try {
                    if (prepared != null) prepared.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                    //throw new RuntimeException(e.getMessage());
                    Debug.Log(LOG_TAG, "ERROR 5 " + e.getLocalizedMessage());
                }
            }
        }
        return false;
    }
}