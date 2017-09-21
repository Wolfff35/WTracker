package com.wolff.wtracker.model;

import java.util.Date;

/**
 * Created by wolff on 15.09.2017.
 */

public class WCoord{
    private double _id;
    private Date _date;
    private WUser _user;
    private String _coord;
    private String _type;

    public WCoord(){

    }

    public double get_id() {
        return _id;
    }

    public void set_id(double _id) {
        this._id = _id;
    }

    public Date get_date() {
        return _date;
    }

    public void set_date(Date _date) {
        this._date = _date;
    }

    public WUser get_user() {
        return _user;
    }

    public void set_user(WUser _user) {
        this._user = _user;
    }

    public String get_coord() {
        return _coord;
    }

    public void set_coord(String _coord) {
        this._coord = _coord;
    }

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }
}
