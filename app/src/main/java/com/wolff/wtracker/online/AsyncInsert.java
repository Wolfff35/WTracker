package com.wolff.wtracker.online;

import android.os.AsyncTask;

import org.json.JSONArray;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.wolff.wtracker.online.AsyncRequest.MSSQL_DB;
import static com.wolff.wtracker.online.AsyncRequest.MSSQL_LOGIN;
import static com.wolff.wtracker.online.AsyncRequest.MSSQL_PASS;

/**
 * Created by wolff on 25.09.2017.
 */
public final class AsyncInsert {/*extends AsyncTask<String, Void, JSONArray> {

    private static final String REMOTE_TABLE = "dbo.TableName";
    private static final String SQL = "INSERT into " + REMOTE_TABLE + "([" +
            ListItemScanned.BARCODE + "],[" + ListItemScanned.NR_ID + "],[" +
            ListItemScanned.DATE + "],[" + ListItemScanned.STATUS + "]) values(?,?,?,?)";

    private final List<ListItemScanned> mData;

    public AsyncInsert(List<ListItemScanned> data) {
        this.mData = data;
    }

    @Override
    protected JSONArray doInBackground(String... proc_params) {
        JSONArray resultSet = new JSONArray();
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            Connection con = null;
            PreparedStatement prepared = null;
            try {
                con = DriverManager.getConnection(MSSQL_DB, MSSQL_LOGIN, MSSQL_PASS);
                if (con != null) {
                    prepared = con.prepareStatement(SQL);

                    for (ListItemScanned item : mData) {
                        prepared.setString(1, item.get(ListItemScanned.BARCODE));
                        prepared.setString(2, item.get(ListItemScanned.NR_ID));
                        prepared.setString(3, item.get(ListItemScanned.DATE));
                        prepared.setString(4, item.get(ListItemScanned.STATUS));
                        prepared.addBatch();
                        resultSet.put(item.get(ListItemScanned.ID));
                    }
                    prepared.executeUpdate();
                    return resultSet;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (prepared != null) prepared.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return resultSet;
    }*/
}
    //https://habrahabr.ru/post/206790/