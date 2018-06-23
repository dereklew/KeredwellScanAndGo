package com.keredwell.scanandgo.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.keredwell.scanandgo.data.C_BP_BankAccount;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class C_BP_BankAccountDBAdapter extends DBAdapter {
    private static final String TAG = makeLogTag(C_BP_BankAccountDBAdapter.class);

    public static final String COLUMN_C_BP_BANKACCOUNT_ID = "_c_bp_bankaccount_id";
    public static final String COLUMN_C_BPARTNER_ID = "_c_bpartner_id";
    public static final String COLUMN_ACCOUNTNO = "_accountno";
    public static final String COLUMN_CREDITCARDTYPE = "_creditcardtype";
    public static final String COLUMN_CREDITCARDNUMBER = "_creditcardnumber";
    public static final String COLUMN_CREDITCARDVV = "_creditcardvv";
    public static final String COLUMN_CREDITCARDEXPMM = "_creditcardexpmm";
    public static final String COLUMN_CREDITCARDEXPYY = "_creditcardexpyy";
    public static final String COLUMN_A_NAME = "_a_name";
    public static final String COLUMN_A_STREET = "_a_street";
    public static final String COLUMN_A_CITY = "_a_city";
    public static final String COLUMN_A_ZIP = "_a_zip";
    public static final String COLUMN_A_COUNTRY = "_a_country";

    public static final String DATABASE_TABLE = "c_bp_bankaccount";

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx
     *            the Context within which to work
     */
    public C_BP_BankAccountDBAdapter(Context ctx) {
        super(ctx);
    }

    /**
     * Create a new customer. If the customer is successfully created return the
     * id for that customer, otherwise return a -1 to indicate failure.
     *
     * @param c_bp_bankAccount
     * @return rowId or -1 if failed
     */
    public long createC_BP_BankAccount(C_BP_BankAccount c_bp_bankAccount){
        open();

        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_C_BPARTNER_ID, c_bp_bankAccount.getC_BPartner_ID());
        initialValues.put(COLUMN_ACCOUNTNO, c_bp_bankAccount.getAccountNo());
        initialValues.put(COLUMN_CREDITCARDTYPE, c_bp_bankAccount.getCreditCardType());
        initialValues.put(COLUMN_CREDITCARDNUMBER, c_bp_bankAccount.getCreditCardNumber());
        initialValues.put(COLUMN_CREDITCARDVV, c_bp_bankAccount.getCreditCardVV());
        initialValues.put(COLUMN_CREDITCARDEXPMM, c_bp_bankAccount.getCreditCardExpMM());
        initialValues.put(COLUMN_CREDITCARDEXPYY, c_bp_bankAccount.getCreditCardExpYY());
        initialValues.put(COLUMN_A_NAME, c_bp_bankAccount.getA_Name());
        initialValues.put(COLUMN_A_STREET, c_bp_bankAccount.getA_Street());
        initialValues.put(COLUMN_A_CITY, c_bp_bankAccount.getA_City());
        initialValues.put(COLUMN_A_ZIP, c_bp_bankAccount.getA_Zip());
        initialValues.put(COLUMN_A_COUNTRY, c_bp_bankAccount.getA_Country());

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the user with the given id
     *
     * @param c_bp_bankAccount_id
     * @return true if deleted, false otherwise
     */
    public boolean deleteC_BP_BankAccount(long c_bp_bankAccount_id) {
        open();

        return mDb.delete(DATABASE_TABLE, COLUMN_C_BP_BANKACCOUNT_ID + "=" + c_bp_bankAccount_id, null) > 0; //$NON-NLS-1$
    }

    /**
     * Return a Cursor positioned at the customer that matches the given rowId
     * @param c_bp_bankAccount_id
     * @return Cursor positioned to matching customer, if found, else null
     */
    public C_BP_BankAccount getC_BP_BankAccount(long c_bp_bankAccount_id) {
        open();

        C_BP_BankAccount c_bp_bankAccount = new C_BP_BankAccount();

        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE, new String[] { COLUMN_C_BP_BANKACCOUNT_ID, COLUMN_C_BPARTNER_ID,
                        COLUMN_ACCOUNTNO, COLUMN_CREDITCARDTYPE, COLUMN_CREDITCARDNUMBER, COLUMN_CREDITCARDVV,
                        COLUMN_CREDITCARDEXPMM, COLUMN_CREDITCARDEXPYY, COLUMN_A_NAME, COLUMN_A_STREET,
                        COLUMN_A_CITY, COLUMN_A_ZIP, COLUMN_A_COUNTRY }, COLUMN_C_BP_BANKACCOUNT_ID + "=" + c_bp_bankAccount_id,
                        null, null, null, null, null);

        if (mCursor != null && mCursor.moveToFirst()) {
            c_bp_bankAccount.setC_Bp_BankAccount_ID(Long.parseLong(mCursor.getString(0)));
            c_bp_bankAccount.setC_BPartner_ID(Long.parseLong(mCursor.getString(1)));
            c_bp_bankAccount.setAccountNo(mCursor.getString(2));
            c_bp_bankAccount.setCreditCardType(mCursor.getString(3));
            c_bp_bankAccount.setCreditCardNumber(mCursor.getString(4));
            c_bp_bankAccount.setCreditCardVV(mCursor.getString(5));
            c_bp_bankAccount.setCreditCardExpMM(Integer.parseInt(mCursor.getString(6)));
            c_bp_bankAccount.setCreditCardExpYY(Integer.parseInt(mCursor.getString(7)));
            c_bp_bankAccount.setA_Name(mCursor.getString(8));
            c_bp_bankAccount.setA_Street(mCursor.getString(9));
            c_bp_bankAccount.setA_City(mCursor.getString(10));
            c_bp_bankAccount.setA_Zip(mCursor.getString(11));
            c_bp_bankAccount.setA_Country(mCursor.getString(12));
        }
        else {
            close();
            return null;
        }
        close();
        return c_bp_bankAccount;
    }

    /**
     * Update the customer.
     *
     * @param c_bp_bankAccount
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateC_BP_BankAccount(C_BP_BankAccount c_bp_bankAccount){
        open();

        ContentValues args = new ContentValues();
        args.put(COLUMN_C_BPARTNER_ID, c_bp_bankAccount.getC_BPartner_ID());
        args.put(COLUMN_ACCOUNTNO, c_bp_bankAccount.getAccountNo());
        args.put(COLUMN_CREDITCARDTYPE, c_bp_bankAccount.getCreditCardType());
        args.put(COLUMN_CREDITCARDNUMBER, c_bp_bankAccount.getCreditCardNumber());
        args.put(COLUMN_CREDITCARDVV, c_bp_bankAccount.getCreditCardVV());
        args.put(COLUMN_CREDITCARDEXPMM, c_bp_bankAccount.getCreditCardExpMM());
        args.put(COLUMN_CREDITCARDEXPYY, c_bp_bankAccount.getCreditCardExpYY());
        args.put(COLUMN_A_NAME, c_bp_bankAccount.getA_Name());
        args.put(COLUMN_A_STREET, c_bp_bankAccount.getA_Street());
        args.put(COLUMN_A_CITY, c_bp_bankAccount.getA_City());
        args.put(COLUMN_A_ZIP, c_bp_bankAccount.getA_Zip());
        args.put(COLUMN_A_COUNTRY, c_bp_bankAccount.getA_Country());

        return mDb.update(DATABASE_TABLE, args, COLUMN_C_BP_BANKACCOUNT_ID + "=" + c_bp_bankAccount.getC_Bp_BankAccount_ID(), null) >0;
    }
}
