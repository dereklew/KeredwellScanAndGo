package com.keredwell.scanandgo.data;

import java.io.Serializable;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class C_BPartner implements Serializable {
    private static final String TAG = makeLogTag(C_BPartner.class);

    private long _c_bpartner_id;
    private long _c_bpartner_location_id;
    private String _name;
    private String _phone;
    private String _address;
    private String _postal;
    private int _rownumber;

    public C_BPartner(){
    }

    public C_BPartner(long c_bpartner_id, String name) {
        this._c_bpartner_id = c_bpartner_id;
        this._name = name;
    }

    public void setC_BPartner_ID(long c_bpartner_id) {
        this._c_bpartner_id = c_bpartner_id;
    }

    public long getC_BPartner_ID() {
        return this._c_bpartner_id;
    }

    public void setC_BPartner_Location_ID(long c_bpartner_location_id) { this._c_bpartner_location_id = c_bpartner_location_id; }

    public long getC_BPartner_Location_ID() {
        return this._c_bpartner_location_id;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getName() {
        return this._name;
    }

    public void setPhone(String phone) {
        this._phone = phone;
    }

    public String getPhone() {
        return this._phone;
    }

    public void setAddress(String address) {
        this._address = address;
    }

    public String getAddress() {
        return this._address;
    }

    public void setPostal(String postal) {
        this._postal = postal;
    }

    public String getPostal() {
        return this._postal;
    }

    public void setRowNumber(int rownumber) {
        this._rownumber = rownumber;
    }

    public int getRowNumber() {
        return this._rownumber;
    }
}
