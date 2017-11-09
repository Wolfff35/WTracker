package com.wolff.wtracker.localdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by wolff on 23.05.2017.
 */

class DbHelper extends SQLiteOpenHelper {
    private  static final int VERSION = 5;

    public DbHelper(Context context) {
        super(context, DbSchema.DATABASE_NAME, null, VERSION);
    }

    @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DbSchema.CREATE_TABLE_USERS);
            db.execSQL(DbSchema.CREATE_TABLE_COORDS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE "+DbSchema.Table_Users.TABLE_NAME);
            db.execSQL("DROP TABLE "+DbSchema.Table_Coords.TABLE_NAME);
            db.execSQL(DbSchema.CREATE_TABLE_USERS);
            db.execSQL(DbSchema.CREATE_TABLE_COORDS);
        }
}
