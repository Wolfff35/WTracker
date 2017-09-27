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
        return user;
     }
    public WCoord getWCoord(){
        WCoord coord = new WCoord();
        DateFormatTools dft = new DateFormatTools();
        coord.set_date(dft.dateFromString(getString(getColumnIndex(DbSchema.Table_Coords.Cols.DATE)),DateFormatTools.DATE_FORMAT_SAVE));
        coord.set_type(getString(getColumnIndex(DbSchema.Table_Coords.Cols.COORD_TYPE)));
        //coord.set_user(getString(getColumnIndex(DbSchema.Table_Coords.Cols.ID_USER)));
        coord.set_coord_lat(getString(getColumnIndex(DbSchema.Table_Coords.Cols.COORD_LAT)));
        coord.set_coord_lon(getString(getColumnIndex(DbSchema.Table_Coords.Cols.COORD_LON)));
        return coord;
    }
 }
