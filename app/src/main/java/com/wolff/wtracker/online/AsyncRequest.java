package com.wolff.wtracker.online;

import android.content.Context;
import android.os.AsyncTask;

import com.wolff.wtracker.localdb.DbSchema;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.tools.Debug;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wolff.wtracker.online.DbSchemaOnline.MSSQL_DB;
import static com.wolff.wtracker.online.DbSchemaOnline.MSSQL_LOGIN;
import static com.wolff.wtracker.online.DbSchemaOnline.MSSQL_PASS;

/**
 * Created by wolfff on 31.10.17.
 */

public class AsyncRequest extends AsyncTask<String,Void,List> {
    private static final String LOG_TAG = "AsyncRequest";
    private Context mContext;
    public AsyncRequest(Context context) {
        this.mContext = context;
    }
    @Override
    protected List doInBackground(String... strings) {
        Debug.Log(LOG_TAG, "Begin");

        Connection con = OnlineDataLab.get(mContext).getOnlineConnection(MSSQL_DB, MSSQL_LOGIN, MSSQL_PASS);
        Statement st = null;
        ResultSet rs=null;
        if (con != null) {
            Debug.Log(LOG_TAG, "Get connection");
            try {
                st = con.createStatement();
                rs = st.executeQuery(strings[0]);

                List<Map<String,Object>> resultList = new ArrayList<>();
                Map<String, Object> row;

                ResultSetMetaData metaData = rs.getMetaData();
                Integer columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    resultList.add(row);
                }
                return resultList;
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
    }
}
