package com.wolff.wtracker.model;

/**
 * Created by wolff on 15.09.2017.
 */

public class WUser {
    private String _id_user;
    private String _name;
    private String _phone;
    private String _imei_phone;
    private String _password;

    public WUser(){

    }

    public String get_id_user() {
        return _id_user;
    }

    public void set_id_user(String _id_user) {
        this._id_user = _id_user;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_phone() {
        return _phone;
    }

    public void set_phone(String _phone) {
        this._phone = _phone;
    }

    public String get_imei_phone() {
        return _imei_phone;
    }

    public void set_imei_phone(String _imei_phone) {
        this._imei_phone = _imei_phone;
    }

    public String get_password() {
        return _password;
    }

    public void set_password(String _password) {
        this._password = _password;
    }
}
