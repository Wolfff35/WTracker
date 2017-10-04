package com.wolff.wtracker.online;

import android.content.Context;
import android.os.AsyncTask;
import com.wolff.wtracker.tools.Debug;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.wolff.wtracker.online.AsyncRequest.MSSQL_DB;
import static com.wolff.wtracker.online.AsyncRequest.MSSQL_LOGIN;
import static com.wolff.wtracker.online.AsyncRequest.MSSQL_PASS;

/**
 * Created by wolff on 03.10.2017.
 */

public class AsyncExecute extends AsyncTask<String, Void, Boolean> {
    private Context mContext;
    public AsyncExecute(Context context) {
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(String... proc_params) {
        if(proc_params[0].isEmpty()){return false;}
        OnlineDataLab onlineDataLab = OnlineDataLab.get(mContext);
        Connection con = onlineDataLab.getOnlineConnection(MSSQL_DB, MSSQL_LOGIN, MSSQL_PASS);
        if (con != null) {
            Statement stm = null;
            try {
                stm = con.createStatement();
                stm.executeUpdate(proc_params[0]);
                return true;
            } catch (SQLException e) {
                Debug.Log("AsyncExecute","ERROR 4 "+e.getLocalizedMessage());

            } finally {
                try {
                    if (stm != null) stm.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                    //throw new RuntimeException(e.getMessage());
                    Debug.Log("AsyncExecute","ERROR 5 "+e.getLocalizedMessage());
                }
            }
        }
        return false;
    }
}
