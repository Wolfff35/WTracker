package com.wolff.wtracker.model;

/**
 * Created by wolff on 15.09.2017.
 */

public class WUser {
    private String _id_user;
    private String _name;
    private String _phone;
    private String _imei_phone;
    private String _pin_for_access;
    private String _password;
    private String _avatar_path;
    private boolean _currentUser;


    public WUser(){

    }

    public String get_pin_for_access() {
        return _pin_for_access;
    }

    public void set_pin_for_access(String _pin_for_access) {
        this._pin_for_access = _pin_for_access;
    }

    public boolean is_currentUser() {
        return _currentUser;
    }

    public void set_currentUser(boolean _currentUser) {
        this._currentUser = _currentUser;
    }

    public String get_avatar_path() {
        return _avatar_path;
    }

    public void set_avatar_path(String _avatar_path) {
        this._avatar_path = _avatar_path;
    }

    public String get_id_user() {
        return _id_user;
    }

    public void set_id_user(String _id_user) {
        this._id_user = _id_user;
    }

    public String get_name() {
        if(_name!=null) {
            return _name;
        }else {
            return "";
        }
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

    @Override
    public String toString() {
        return "WUser{" +
                "_id_user='" + _id_user + '\'' +
                ", _name='" + _name + '\'' +
                ", _phone='" + _phone + '\'' +
                ", _imei_phone='" + _imei_phone + '\'' +
                ", _password='" + _password + '\'' +
                ", _avatar_path='" + _avatar_path + '\'' +
                ", _currentUser=" + _currentUser +
                ", _pin_for_access=" + _pin_for_access +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WUser)) return false;

        WUser wUser = (WUser) o;

        if (!get_id_user().equals(wUser.get_id_user())) return false;
        if (!get_phone().equals(wUser.get_phone())) return false;
        //if (!get_imei_phone().equals(wUser.get_imei_phone())) return false;
        return get_imei_phone().equals(wUser.get_imei_phone());
        //if (!get_pin_for_access().equals(wUser.get_pin_for_access())) return false;
        //return get_password().equals(wUser.get_password());

    }

    @Override
    public int hashCode() {
        int result = get_id_user().hashCode();
        result = 31 * result + get_phone().hashCode();
        result = 31 * result + get_imei_phone().hashCode();
        result = 31 * result + get_pin_for_access().hashCode();
        result = 31 * result + get_password().hashCode();
        return result;
    }
}
