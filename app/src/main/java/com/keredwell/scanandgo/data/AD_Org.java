package com.keredwell.scanandgo.data;

import java.io.Serializable;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class AD_Org implements Serializable {
    private static final String TAG = makeLogTag(AD_Org.class);

    private long _ad_org_id;
    private String _value;
    private String _name;
    private int _rownumber;

    public AD_Org(){
    }

    public AD_Org(long ad_org_id, String value, String name) {
        this._ad_org_id = ad_org_id;
        this._value = value;
        this._name = name;
    }

    public void setAD_Org_ID(long ad_org_id) { this._ad_org_id = ad_org_id; }

    public long getAD_Org_ID() {
        return this._ad_org_id;
    }

    public void setValue(String value) { this._value = value; }

    public String getValue() {
        return this._value;
    }

    public void setName(String name) { this._name = name; }

    public String getName() {
        return this._name;
    }

    public void setRowNumber(int rownumber) {
        this._rownumber = rownumber;
    }

    public int getRowNumber() {
        return this._rownumber;
    }
}
