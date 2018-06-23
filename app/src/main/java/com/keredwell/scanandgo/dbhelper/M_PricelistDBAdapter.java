package com.keredwell.scanandgo.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.keredwell.scanandgo.data.M_Pricelist;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class M_PricelistDBAdapter extends DBAdapter {
    private static final String TAG = makeLogTag(M_PricelistDBAdapter.class);

    public static final String COLUMN_M_PRICELIST_ID = "_m_pricelist_id";
    public static final String COLUMN_AD_ORG_ID = "_ad_org_id";
    public static final String COLUMN_NAME = "_name";

    public static final String DATABASE_TABLE = "m_pricelist";

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx
     *            the Context within which to work
     */
    public M_PricelistDBAdapter(Context ctx) {
        super(ctx);
    }

    /**
     * Create a new customer. If the customer is successfully created return the
     * id for that customer, otherwise return a -1 to indicate failure.
     *
     * @param m_pricelist
     * @return rowId or -1 if failed
     */
    public long createM_Pricelist(M_Pricelist m_pricelist){
        open();

        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_M_PRICELIST_ID, m_pricelist.getM_Pricelist_ID());
        initialValues.put(COLUMN_AD_ORG_ID, m_pricelist.getAd_Org_ID());
        initialValues.put(COLUMN_NAME, m_pricelist.getName());

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the user with the given id
     *
     * @param m_picelist_id
     * @return true if deleted, false otherwise
     */
    public boolean deleteM_Pricelist(long m_picelist_id) {
        open();

        return mDb.delete(DATABASE_TABLE, COLUMN_M_PRICELIST_ID + "=" + m_picelist_id, null) > 0; //$NON-NLS-1$
    }

    /**
     * Return a Cursor positioned at the customer that matches the given rowId
     * @param m_picelist_id
     * @return Cursor positioned to matching customer, if found, else null
     */
    public M_Pricelist getM_Pricelist(long m_picelist_id) {
        open();

        M_Pricelist m_productPrice = new M_Pricelist();

        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE, new String[] { COLUMN_M_PRICELIST_ID, COLUMN_AD_ORG_ID, COLUMN_NAME },
                        COLUMN_M_PRICELIST_ID + "=" + m_picelist_id,
                        null, null, null, null, null);

        if (mCursor != null && mCursor.moveToFirst()) {
            m_productPrice.setM_Pricelist_ID(Long.parseLong(mCursor.getString(0)));
            m_productPrice.setAd_Org_ID(Long.parseLong(mCursor.getString(1)));
            m_productPrice.setName(mCursor.getString(2));
            m_productPrice.setRowNumber(0);
        }
        else {
            close();
            return null;
        }
        close();
        return m_productPrice;
    }

    /**
     * Return a Cursor positioned at the customer that matches the given rowId
     * @param ad_org_id
     * @return Cursor positioned to matching customer, if found, else null
     */
    public M_Pricelist getM_PricelistByAD_Org_Id(long ad_org_id) {
        open();

        M_Pricelist m_productPrice = new M_Pricelist();

        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE, new String[] { COLUMN_M_PRICELIST_ID, COLUMN_AD_ORG_ID, COLUMN_NAME },
                        COLUMN_AD_ORG_ID + "=" + ad_org_id,
                        null, null, null, null, null);

        if (mCursor != null && mCursor.moveToFirst()) {
            m_productPrice.setM_Pricelist_ID(Long.parseLong(mCursor.getString(0)));
            m_productPrice.setAd_Org_ID(Long.parseLong(mCursor.getString(1)));
            m_productPrice.setName(mCursor.getString(2));
            m_productPrice.setRowNumber(0);
        }
        else {
            close();
            return null;
        }
        close();
        return m_productPrice;
    }

    /**
     * Update the customer.
     *
     * @param m_pricelist
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateM_Pricelist(M_Pricelist m_pricelist){
        open();

        ContentValues args = new ContentValues();
        args.put(COLUMN_AD_ORG_ID, m_pricelist.getAd_Org_ID());
        args.put(COLUMN_NAME, m_pricelist.getName());

        return mDb.update(DATABASE_TABLE, args, COLUMN_M_PRICELIST_ID + "=" + m_pricelist.getM_Pricelist_ID(), null) >0;
    }
}
