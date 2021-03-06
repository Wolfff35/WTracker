package com.wolff.wtracker.localdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.tools.DateFormatTools;
import com.wolff.wtracker.tools.Debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wolff on 23.05.2017.
 */

public class DataLab {
    private static DataLab sDataLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private DataLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new DbHelper(mContext).getWritableDatabase();
    }

    public static DataLab get(Context context) {
        if (sDataLab == null) {
            sDataLab = new DataLab(context);
        }
        return sDataLab;
    }

    //--------------------------------------------------------------------------------------------------
    private DbCursorWrapper queryWCoords() {
        String selection = null;
        String[] selectionArgs = null;
        String[] columns = null;
        String groupBy = null;
        String having = null;
        String orderBy = DbSchema.Table_Coords.Cols.DATE + " DESC";
        Cursor cursor = mDatabase.query(DbSchema.Table_Coords.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy);
        return new DbCursorWrapper(cursor);
    }

    private static ContentValues getContentValues_WCoords(WUser user, WCoord coord) {
        ContentValues values = new ContentValues();
        DateFormatTools dft = new DateFormatTools();
        values.put(DbSchema.Table_Coords.Cols.DATE, dft.dateToString(coord.get_date(), DateFormatTools.DATE_FORMAT_SAVE));
        values.put(DbSchema.Table_Coords.Cols.COORD_LAT, coord.get_coord_lat());
        values.put(DbSchema.Table_Coords.Cols.COORD_LON, coord.get_coord_lon());
        values.put(DbSchema.Table_Coords.Cols.COORD_PROVIDER, coord.get_provider());
        if (user != null) {
            values.put(DbSchema.Table_Users.Cols.ID_USER, user.get_id_user());
        }
        values.put(DbSchema.Table_Coords.Cols.COORD_ACCURACY, coord.get_accuracy());
        values.put(DbSchema.Table_Coords.Cols.COORD_ALTITUDE, coord.get_altitude());
        values.put(DbSchema.Table_Coords.Cols.COORD_BEARING, coord.get_bearing());
        return values;
    }

    public void coord_add(WUser user, WCoord coord) {
        ContentValues values = getContentValues_WCoords(user, coord);
        mDatabase.insert(DbSchema.Table_Coords.TABLE_NAME, null, values);
    }

    public void coord_delete(WCoord coord) {
        mDatabase.delete(
                DbSchema.Table_Coords.TABLE_NAME,
                DbSchema.Table_Coords.Cols.ID + " =?",
                new String[]{String.valueOf(coord.get_id())}
        );
    }

    public ArrayList<WCoord> getLocalCoords() {
        DbCursorWrapper cursorWrapper = queryWCoords();
        ArrayList<WCoord> coordList = new ArrayList<>();
        cursorWrapper.moveToFirst();
        while (!cursorWrapper.isAfterLast()) {
            WCoord coord = cursorWrapper.getWCoord();
            coordList.add(coord);
            cursorWrapper.moveToNext();
        }
        cursorWrapper.close();
        return coordList;
    }

    //--------------------------------------------------------------------------------------------------
    private DbCursorWrapper queryWUsers() {
        String selection = null;
        String[] selectionArgs = null;
        String[] columns = null;
        String groupBy = null;
        String having = null;
        String orderBy = DbSchema.Table_Users.Cols.PHONE + " DESC";
        Cursor cursor = mDatabase.query(DbSchema.Table_Users.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy);
        return new DbCursorWrapper(cursor);
    }

    public WUser queryWUserById(String id) {
        String selection = DbSchema.Table_Users.Cols.ID_USER + " = ?";
        String[] selectionArgs = new String[]{id};

        String[] columns = null;
        String groupBy = null;
        String having = null;
        String orderBy = DbSchema.Table_Users.Cols.PHONE + " DESC";
        Cursor cursor = mDatabase.query(DbSchema.Table_Users.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy);
        DbCursorWrapper cursorWrapper = new DbCursorWrapper(cursor);
        WUser user = null;
        if (cursorWrapper.moveToFirst()) {
            user = cursorWrapper.getWUser();
        }
        cursorWrapper.close();
        return user;
    }

    private static ContentValues getContentValues_WUsers(WUser user) {
        ContentValues values = new ContentValues();
        values.put(DbSchema.Table_Users.Cols.PHONE, user.get_phone());
        values.put(DbSchema.Table_Users.Cols.IMEI_PHONE, user.get_imei_phone());
        values.put(DbSchema.Table_Users.Cols.ID_USER, user.get_id_user());
        values.put(DbSchema.Table_Users.Cols.NAME, user.get_name());
        values.put(DbSchema.Table_Users.Cols.PASSWORD, user.get_password());
        values.put(DbSchema.Table_Users.Cols.AVATAR_PATH, user.get_avatar_path());
        values.put(DbSchema.Table_Users.Cols.CURRENT, user.is_currentUser());
        values.put(DbSchema.Table_Users.Cols.PIN_FOR_ACCESS, user.get_pin_for_access());
        return values;
    }

    public void user_add(WUser user) {
        ContentValues values = getContentValues_WUsers(user);
        mDatabase.insert(DbSchema.Table_Users.TABLE_NAME, null, values);
    }

    public void user_delete(WUser user) {
        mDatabase.delete(
                DbSchema.Table_Users.TABLE_NAME,
                DbSchema.Table_Users.Cols.ID_USER + " =?",
                new String[]{String.valueOf(user.get_id_user())}
        );
    }

    //==================================================================================================
    public WUser getUserById(String id, ArrayList<WUser> userList) {
        for (WUser user : userList) {
            if (user.get_id_user().equalsIgnoreCase(id)) {
                return user;
            }
        }
        return null;
    }

    public WUser getCurrentUser(ArrayList<WUser> userList) {
        for (WUser user : userList) {
            if (user.is_currentUser()) {
                return user;
            }
        }
        return null;
    }

    public ArrayList<WUser> getWUserList() {
        DbCursorWrapper cursorWrapper = queryWUsers();
        ArrayList<WUser> userList = new ArrayList<>();
        cursorWrapper.moveToFirst();
        while (!cursorWrapper.isAfterLast()) {
            WUser user = cursorWrapper.getWUser();
            userList.add(user);
            cursorWrapper.moveToNext();
        }
        cursorWrapper.close();
        return userList;
    }

}
