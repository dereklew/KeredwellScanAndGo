package com.keredwell.scanandgo.data;

import java.io.Serializable;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class C_BP_BankAccount implements Serializable {
    private static final String TAG = makeLogTag(C_BP_BankAccount.class);

    private long _c_bp_bankaccount_id;
    private long _c_bpartner_id;
    private String _accountno;
    private String _creditcardtype;
    private String _creditcardnumber;
    private String _creditcardvv;
    private int _creditcardexpmm;
    private int _creditcardexpyy;
    private String _a_name;
    private String _a_street;
    private String _a_city;
    private String _a_zip;
    private String _a_country;

    public C_BP_BankAccount(){
    }

    public C_BP_BankAccount(long c_bpartner_id, String accountno, String creditcardtype) {
        this._c_bpartner_id = c_bpartner_id;
        this._accountno = accountno;
        this._creditcardtype = creditcardtype;
    }

    public void setC_Bp_BankAccount_ID(long c_bp_bankaccount_id) { this._c_bp_bankaccount_id = c_bp_bankaccount_id; }

    public long getC_Bp_BankAccount_ID() {
        return this._c_bp_bankaccount_id;
    }

    public void setC_BPartner_ID(long c_bpartner_id) {
        this._c_bpartner_id = c_bpartner_id;
    }

    public long getC_BPartner_ID() {
        return this._c_bpartner_id;
    }

    public void setAccountNo(String accountno) {
        this._accountno = accountno;
    }

    public String getAccountNo() {
        return this._accountno;
    }

    public void setCreditCardType(String creditcardtype) {
        this._creditcardtype = creditcardtype;
    }

    public String getCreditCardType() {
        return this._creditcardtype;
    }

    public void setCreditCardNumber(String creditcardnumber) { this._creditcardnumber = creditcardnumber; }

    public String getCreditCardNumber() {
        return this._creditcardnumber;
    }

    public void setCreditCardVV(String creditcardvv) { this._creditcardvv = creditcardvv; }

    public String getCreditCardVV() {
        return this._creditcardvv;
    }

    public void setCreditCardExpMM(int creditcardexpmm) { this._creditcardexpmm = creditcardexpmm; }

    public int getCreditCardExpMM() {
        return this._creditcardexpmm;
    }

    public void setCreditCardExpYY(int creditcardexpyy) { this._creditcardexpyy = creditcardexpyy; }

    public int getCreditCardExpYY() {
        return this._creditcardexpyy;
    }

    public void setA_Name(String a_name) { this._a_name = a_name; }

    public String getA_Name() {
        return this._a_name;
    }

    public void setA_Street(String a_street) { this._a_street = a_street; }

    public String getA_Street() {
        return this._a_street;
    }

    public void setA_City(String a_city) { this._a_city = a_city; }

    public String getA_City() { return this._a_city; }

    public void setA_Zip(String a_zip) { this._a_zip = a_zip; }

    public String getA_Zip() { return this._a_zip; }

    public void setA_Country(String a_country) { this._a_country = a_country; }

    public String getA_Country() { return this._a_country; }
}
