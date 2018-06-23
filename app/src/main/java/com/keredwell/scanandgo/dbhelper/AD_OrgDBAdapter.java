package com.keredwell.scanandgo.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.keredwell.scanandgo.data.AD_Org;
import com.keredwell.scanandgo.data.C_BPartner_Location;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class AD_OrgDBAdapter extends DBAdapter {
    private static final String TAG = makeLogTag(AD_OrgDBAdapter.class);

    public static final String COLUMN_AD_ORG_ID = "_ad_org_id";
    public static final String COLUMN_VALUE = "_value";
    public static final String COLUMN_NAME = "_name";

    public static final String DATABASE_TABLE = "ad_org";

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx
     *            the Context within which to work
     */
    public AD_OrgDBAdapter(Context ctx) {
        super(ctx);
    }

    /**
     * Create a new customer. If the customer is successfully created return the
     * id for that customer, otherwise return a -1 to indicate failure.
     *
     * @param ad_org
     * @return rowId or -1 if failed
     */
    public long createAD_Org(AD_Org ad_org){
        open();

        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_AD_ORG_ID, ad_org.getAD_Org_ID());
        initialValues.put(COLUMN_VALUE, ad_org.getValue());
        initialValues.put(COLUMN_NAME, ad_org.getValue());

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the user with the given id
     *
     * @param ad_org_id
     * @return true if deleted, false otherwise
     */
    public boolean deleteAD_Org(long ad_org_id) {
        open();

        return mDb.delete(DATABASE_TABLE, COLUMN_AD_ORG_ID + "=" + ad_org_id, null) > 0; //$NON-NLS-1$
    }

    /**
     * Return a Cursor positioned at the customer that matches the given rowId
     * @param ad_org_id
     * @return Cursor positioned to matching customer, if found, else null
     */
    public AD_Org getAD_Org(long ad_org_id) {
        open();

        AD_Org ad_org = new AD_Org();

        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE, new String[] { COLUMN_AD_ORG_ID, COLUMN_VALUE,
                                COLUMN_NAME }, COLUMN_AD_ORG_ID + "=" + ad_org_id,
                        null, null, null, null, null);

        if (mCursor != null && mCursor.moveToFirst()) {
            ad_org.setAD_Org_ID(Long.parseLong(mCursor.getString(0)));
            ad_org.setValue(mCursor.getString(1));
            ad_org.setName(mCursor.getString(2));
            ad_org.setRowNumber(0);
        }
        else {
            close();
            return null;
        }
        close();
        return ad_org;
    }

    /**
     * Return a Cursor positioned at the customer that matches the given rowId
     * @param value
     * @return Cursor positioned to matching customer, if found, else null
     */
    public AD_Org getAD_Org(String value) {
        open();

        AD_Org ad_org = new AD_Org();

        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE, new String[] { COLUMN_AD_ORG_ID, COLUMN_VALUE,
                                COLUMN_NAME }, COLUMN_VALUE + "=" + value,
                        null, null, null, null, null);

        if (mCursor != null && mCursor.moveToFirst()) {
            ad_org.setAD_Org_ID(Long.parseLong(mCursor.getString(0)));
            ad_org.setValue(mCursor.getString(1));
            ad_org.setName(mCursor.getString(2));
            ad_org.setRowNumber(0);
        }
        else {
            close();
            return null;
        }
        close();
        return ad_org;
    }

    /**
     * Update the customer.
     *
     * @param ad_org
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateAD_Org(AD_Org ad_org){
        open();

        ContentValues args = new ContentValues();
        args.put(COLUMN_VALUE, ad_org.getValue());
        args.put(COLUMN_NAME, ad_org.getName());

        return mDb.update(DATABASE_TABLE, args, COLUMN_AD_ORG_ID + "=" + ad_org.getAD_Org_ID(), null) >0;
    }
}
