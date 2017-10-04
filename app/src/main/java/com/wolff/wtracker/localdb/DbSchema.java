package com.wolff.wtracker.localdb;

/**
 * Created by wolff on 23.05.2017.
 */

public class DbSchema {

    //==========================================================================================
    public static final String DATABASE_NAME = "wtracker.db";
    public static final String CREATE_TABLE_LAST_COORDS = "CREATE TABLE "+ Table_LastCoords.TABLE_NAME+" ("+
            Table_Coords.Cols.ID            +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            Table_Coords.Cols.DATE          +" TEXT, "+
            Table_Users.Cols.ID_USER       +" TEXT, "+
            Table_Coords.Cols.COORD_PROVIDER +" TEXT, "+
            Table_Coords.Cols.COORD_LAT       +" INTEGER, "+
            Table_Coords.Cols.COORD_LON       +" INTEGER, "+
            Table_Coords.Cols.COORD_ALTITUDE       +" INTEGER, "+
            Table_Coords.Cols.COORD_ACCURACY       +" INTEGER, "+
            Table_Coords.Cols.COORD_BEARING       +" INTEGER, "+
            "FOREIGN KEY ("+ Table_Users.Cols.ID_USER+
            ") REFERENCES "+ Table_Users.TABLE_NAME+"("+
            Table_Users.Cols.ID_USER+")"+
            ")";

    public static final String CREATE_TABLE_COORDS = "CREATE TABLE "+ Table_Coords.TABLE_NAME+" ("+
            Table_Coords.Cols.ID            +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            Table_Coords.Cols.DATE          +" TEXT, "+
            Table_Users.Cols.ID_USER       +" TEXT, "+
            Table_Coords.Cols.COORD_PROVIDER +" TEXT, "+
            Table_Coords.Cols.COORD_LAT       +" INTEGER, "+
            Table_Coords.Cols.COORD_LON       +" INTEGER, "+
            Table_Coords.Cols.COORD_ALTITUDE       +" INTEGER, "+
            Table_Coords.Cols.COORD_ACCURACY       +" INTEGER, "+
            Table_Coords.Cols.COORD_BEARING       +" INTEGER, "+
            "FOREIGN KEY ("+ Table_Users.Cols.ID_USER+
            ") REFERENCES "+ Table_Users.TABLE_NAME+"("+
            Table_Users.Cols.ID_USER+")"+
            ")";

    public static final String CREATE_TABLE_USERS = "CREATE TABLE "+ Table_Users.TABLE_NAME+" ("+
            Table_Users.Cols.ID_USER        +" TEXT PRIMARY KEY NOT NULL, "+
            Table_Users.Cols.NAME +" TEXT, "+
            Table_Users.Cols.PHONE +" TEXT, "+
            Table_Users.Cols.PASSWORD +" TEXT, "+
            Table_Users.Cols.IMEI_PHONE +" TEXT, "+
            Table_Users.Cols.PIN_FOR_ACCESS +" TEXT, "+
            Table_Users.Cols.CURRENT +" INTEGER, "+
            Table_Users.Cols.AVATAR_PATH +" TEXT "+
            ")";

    //==================================================================================================
     public static final class Table_Coords{

         public static final String TABLE_NAME = "table_coord";

         public static final class Cols{
             public static final String ID          = "_id";
             public static final String DATE        = "_date";
             //public static final String ID_USER     = "_id_user";
             public static final String COORD_LAT       = "_coord_lat";
             public static final String COORD_LON       = "_coord_lon";
             public static final String COORD_PROVIDER = "_provider";
             public static final String COORD_ALTITUDE  = "_altitude";
             public static final String COORD_ACCURACY  = "_accuracy";
             public static final String COORD_BEARING  = "_bearing";
         }

     }
    public static final class Table_LastCoords{

        public static final String TABLE_NAME = "table_last_coord";
    }

    public static final class Table_Users{

        public static final String TABLE_NAME = "table_users";

        public static final class Cols{
            //public static final String ID             = "_id";
            public static final String ID_USER          = "_id_user";
            public static final String NAME             = "_name";
            public static final String PHONE            = "_phone";
            public static final String IMEI_PHONE       = "_id_phone";
            public static final String PIN_FOR_ACCESS   = "_pin_for_access";
            public static final String PASSWORD         = "_password";
            public static final String AVATAR_PATH      = "_avatar";
            public static final String CURRENT          = "_current";
        }
    }
}
