package com.wolff.wtracker.localdb;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.tools.DateFormatTools;


/**
 * Created by wolff on 23.05.2017.
 */

class DbCursorWrapper extends CursorWrapper {

      public DbCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public WUser getWUser(){
        WUser user = new WUser();
        user.set_id_user(getString(getColumnIndex(DbSchema.Table_Users.Cols.ID_USER)));
        user.set_imei_phone(getString(getColumnIndex(DbSchema.Table_Users.Cols.IMEI_PHONE)));
        user.set_name(getString(getColumnIndex(DbSchema.Table_Users.Cols.NAME)));
        user.set_password(getString(getColumnIndex(DbSchema.Table_Users.Cols.PASSWORD)));
        user.set_phone(getString(getColumnIndex(DbSchema.Table_Users.Cols.PHONE)));
        user.setAvatar_path(getString(getColumnIndex(DbSchema.Table_Users.Cols.AVATAR_PATH)));
        return user;
     }
    public WCoord getWCoord(){
        WCoord coord = new WCoord();
        DateFormatTools dft = new DateFormatTools();
        coord.set_date(dft.dateFromString(getString(getColumnIndex(DbSchema.Table_Coords.Cols.DATE)),DateFormatTools.DATE_FORMAT_SAVE));
        coord.set_provider(getString(getColumnIndex(DbSchema.Table_Coords.Cols.COORD_PROVIDER)));
        //coord.set_user(getString(getColumnIndex(DbSchema.Table_Coords.Cols.ID_USER)));
        coord.set_coord_lat(getDouble(getColumnIndex(DbSchema.Table_Coords.Cols.COORD_LAT)));
        coord.set_coord_lon(getDouble(getColumnIndex(DbSchema.Table_Coords.Cols.COORD_LON)));
        coord.set_accuracy(getDouble(getColumnIndex(DbSchema.Table_Coords.Cols.COORD_ACCURACY)));
        coord.set_altitude(getDouble(getColumnIndex(DbSchema.Table_Coords.Cols.COORD_ALTITUDE)));
        coord.set_bearing(getDouble(getColumnIndex(DbSchema.Table_Coords.Cols.COORD_BEARING)));
        return coord;
    }
 }
