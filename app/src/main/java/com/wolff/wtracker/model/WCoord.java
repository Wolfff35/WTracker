package com.wolff.wtracker.model;

import android.location.Location;

import java.util.Date;

/**
 * Created by wolff on 15.09.2017.
 */

public class WCoord{
    private double _id;
    private Date _date;
    private double _coord_lat;
    private double _coord_lon;
    private String _provider;

    private double accuracy; //точность
    private double altitude; //высота над уровнем моря
    private double bearing;


    public WCoord(){

    }
    public WCoord(Location loc){
        if(loc!=null) {
            this.bearing = loc.getBearing();
            this.altitude = loc.getAltitude();
            this.accuracy = loc.getAccuracy();
            this._provider = loc.getProvider();
            this._coord_lat = loc.getLatitude();
            this._coord_lon = loc.getLongitude();
            this._date = new Date(loc.getTime());
         }

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

    public double get_coord_lat() {
        return _coord_lat;
    }

    public void set_coord_lat(double _coord) {
        this._coord_lat = _coord;
    }

    public double get_coord_lon() {
        return _coord_lon;
    }

    public void set_coord_lon(double _coord) {
        this._coord_lon = _coord;
    }

    public String get_provider() {
        return _provider;
    }

    public void set_provider(String _provider) {
        this._provider = _provider;
    }

    public double get_accuracy() {
        return accuracy;
    }

    public void set_accuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double get_altitude() {
        return altitude;
    }

    public void set_altitude(double altitude) {
        this.altitude = altitude;
    }

    public double get_bearing() {
        return bearing;
    }

    public void set_bearing(double bearing) {
        this.bearing = bearing;
    }

    @Override
    public String toString() {
        return "WCoord{" +
                "_id=" + _id +
                ", _date=" + _date +
                ", _coord_lat=" + _coord_lat +
                ", _coord_lon=" + _coord_lon +
                ", _provider='" + _provider + '\'' +
                ", accuracy=" + accuracy +
                ", altitude=" + altitude +
                ", bearing=" + bearing +
                '}';
    }
}
