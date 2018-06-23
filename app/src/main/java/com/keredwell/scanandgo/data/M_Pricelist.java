package com.keredwell.scanandgo.data;

import java.io.Serializable;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class M_Pricelist implements Serializable {
    private static final String TAG = makeLogTag(M_Pricelist.class);

    private long _m_pricelist_id;
    private long _ad_org_id;
    private String _name;
    private int _rownumber;

    public M_Pricelist(){
    }

    public M_Pricelist(long m_pricelist_id, long ad_org_id, String name) {
        this._m_pricelist_id = m_pricelist_id;
        this._ad_org_id = ad_org_id;
        this._name = name;
    }

    public void setM_Pricelist_ID(long m_pricelist_id) {
        this._m_pricelist_id = m_pricelist_id;
    }

    public long getM_Pricelist_ID() {
        return this._m_pricelist_id;
    }

    public void setAd_Org_ID(long ad_org_id) {
        this._ad_org_id = ad_org_id;
    }

    public long getAd_Org_ID() {
        return this._ad_org_id;
    }

    public void setName(String name) {
        this._name = name;
    }

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
