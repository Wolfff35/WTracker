package com.wolff.wtracker.online;

import com.wolff.wtracker.localdb.DbSchema;

/**
 * Created by wolff on 03.10.2017.
 */

public class DbSchemaOnline {
    public static final String CREATE_TABLE_USERS = "CREATE TABLE [tessst_gps].[dbo].[t_users] ("+
            DbSchema.Table_Users.Cols.ID_USER        +" VARCHAR(14) PRIMARY KEY NOT NULL, "+
            DbSchema.Table_Users.Cols.PASSWORD +" VARCHAR(20), "+
            DbSchema.Table_Users.Cols.IMEI_PHONE +" VARCHAR(20), "+
            DbSchema.Table_Users.Cols.PIN_FOR_ACCESS+" VARCHAR(10)"+ //пин для подключения другими юзерами
            ")";

    public static final String CREATE_TABLE_COORDS = "CREATE TABLE [tessst_gps].[dbo].[t_coords] ("+
            DbSchema.Table_Coords.Cols.DATE          +" DATETIME NOT NULL, "+
            DbSchema.Table_Users.Cols.ID_USER       +" VARCHAR(14) NOT NULL, "+
            DbSchema.Table_Users.Cols.IMEI_PHONE       +" VARCHAR(20), "+
            DbSchema.Table_Coords.Cols.COORD_PROVIDER +" VARCHAR(10) NOT NULL, "+
            DbSchema.Table_Coords.Cols.COORD_LAT       +" FLOAT NOT NULL, "+
            DbSchema.Table_Coords.Cols.COORD_LON       +" FLOAT NOT NULL, "+
            DbSchema.Table_Coords.Cols.COORD_ALTITUDE       +" FLOAT, "+
            DbSchema.Table_Coords.Cols.COORD_ACCURACY       +" FLOAT, "+
            DbSchema.Table_Coords.Cols.COORD_BEARING       +" FLOAT, "+
            "FOREIGN KEY ("+ DbSchema.Table_Users.Cols.ID_USER+
            ") REFERENCES [tessst_gps].[dbo].[t_users] ("+
            DbSchema.Table_Users.Cols.ID_USER+")"+
            ")";
}

/*
if not exists (select * from sysobjects where name='cars' and xtype='U')
    create table cars (
        Name varchar(64) not null
    )
go
 */