package com.wolff.wtracker.online;

import android.content.Context;

import com.wolff.wtracker.localdb.DbSchema;
import com.wolff.wtracker.tools.Debug;

import java.util.concurrent.ExecutionException;

/**
 * Created by wolff on 03.10.2017.
 */

public class DbSchemaOnline {
    public final static String MSSQL_DB = "jdbc:jtds:sqlserver://13.10.12.10:1433:/tessst_gps;";
    public final static String MSSQL_LOGIN = "sa";
    public final static String MSSQL_PASS = "Rfcf,kfyrf";


    public static final String CREATE_TABLE_USERS = "CREATE TABLE [tessst_gps].[dbo].[t_users] (" +
            DbSchema.Table_Users.Cols.ID_USER + " VARCHAR(14) PRIMARY KEY NOT NULL, " +
            DbSchema.Table_Users.Cols.PASSWORD + " VARCHAR(20), " +
            DbSchema.Table_Users.Cols.NAME + " VARCHAR(20), " +
            DbSchema.Table_Users.Cols.IMEI_PHONE + " VARCHAR(20), " +
            DbSchema.Table_Users.Cols.PIN_FOR_ACCESS + " VARCHAR(10)" + //пин для подключения другими юзерами
            ")";

    public static final String CREATE_TABLE_COORDS = "CREATE TABLE [tessst_gps].[dbo].[t_coords] (" +
            //DbSchema.Table_Coords.Cols.DATE          +" DATETIME NOT NULL, "+
            DbSchema.Table_Coords.Cols.DATE + " DATE NOT NULL, " +
            DbSchema.Table_Coords.Cols.TIME + " TIME NOT NULL, " +
            DbSchema.Table_Users.Cols.ID_USER + " VARCHAR(14) NOT NULL, " +
            DbSchema.Table_Users.Cols.IMEI_PHONE + " VARCHAR(20), " +
            DbSchema.Table_Coords.Cols.COORD_PROVIDER + " VARCHAR(10) NOT NULL, " +
            DbSchema.Table_Coords.Cols.COORD_LAT + " FLOAT NOT NULL, " +
            DbSchema.Table_Coords.Cols.COORD_LON + " FLOAT NOT NULL, " +
            DbSchema.Table_Coords.Cols.COORD_ALTITUDE + " FLOAT, " +
            DbSchema.Table_Coords.Cols.COORD_ACCURACY + " FLOAT, " +
            DbSchema.Table_Coords.Cols.COORD_BEARING + " FLOAT, " +
            "FOREIGN KEY (" + DbSchema.Table_Users.Cols.ID_USER +
            ") REFERENCES [tessst_gps].[dbo].[t_users] (" +
            DbSchema.Table_Users.Cols.ID_USER + ")" +
            ")";


    public void create_online_tables(Context context) {
        try {
            AsyncExecute ac = new AsyncExecute(context);
            boolean flag = ac.execute(DbSchemaOnline.CREATE_TABLE_USERS).get();
            Debug.Log("AsyncExecute", "flag users = " + flag);
            AsyncExecute ac2 = new AsyncExecute(context);
            boolean flag2 = ac2.execute(DbSchemaOnline.CREATE_TABLE_COORDS).get();
            Debug.Log("AsyncExecute", "flag coords = " + flag2);
        } catch (InterruptedException e) {
            Debug.Log("AsyncExecute", "ERROR 1 " + e.getLocalizedMessage());
        } catch (ExecutionException e) {
            Debug.Log("AsyncExecute", "ERROR 2 " + e.getLocalizedMessage());
        }
    }
}
/*
if not exists (select * from sysobjects where name='cars' and xtype='U')
    create table cars (
        Name varchar(64) not null
    )
go
 */