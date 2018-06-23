package com.keredwell.scanandgo.webservice;

import com.keredwell.scanandgo.data.C_BP_BankAccount;
import com.keredwell.scanandgo.util.DateUtil;
import com.keredwell.scanandgo.util.LogUtil;
import com.keredwell.scanandgo.util.PropUtil;

import org.ksoap2.serialization.SoapObject;

import java.math.BigDecimal;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class C_BP_BankAccountWS {
    private static final String TAG = makeLogTag(C_BP_BankAccountWS.class);

    public static long WSEvent(String mUser, String mPassword, C_BP_BankAccount c_bp_bankAccount)
    {
        try{
            SoapObject C_BPartner_ID = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            C_BPartner_ID.addAttribute("column", "C_BPartner_ID");
            C_BPartner_ID.addProperty("val", Long.toString(c_bp_bankAccount.getC_BPartner_ID()));

            SoapObject AccountNo = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            AccountNo.addAttribute("column", "AccountNo");
            AccountNo.addProperty("val", c_bp_bankAccount.getAccountNo());

            SoapObject CreditCardType = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            CreditCardType.addAttribute("column", "CreditCardType");
            CreditCardType.addProperty("val", c_bp_bankAccount.getCreditCardType());

            SoapObject CreditCardNumber = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            CreditCardNumber.addAttribute("column", "CreditCardNumber");
            CreditCardNumber.addProperty("val", c_bp_bankAccount.getCreditCardNumber());

            SoapObject CreditCardVV = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            CreditCardVV.addAttribute("column", "CreditCardVV");
            CreditCardVV.addProperty("val", c_bp_bankAccount.getCreditCardVV());

            SoapObject CreditCardExpMM = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            CreditCardExpMM.addAttribute("column", "CreditCardExpMM");
            CreditCardExpMM.addProperty("val", Integer.toString(c_bp_bankAccount.getCreditCardExpMM()));

            SoapObject CreditCardExpYY = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            CreditCardExpYY.addAttribute("column", "CreditCardExpYY");
            CreditCardExpYY.addProperty("val", Integer.toString(c_bp_bankAccount.getCreditCardExpYY()));

            SoapObject A_Name = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            A_Name.addAttribute("column", "A_Name");
            A_Name.addProperty("val", c_bp_bankAccount.getA_Name());

            SoapObject A_Street = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            A_Street.addAttribute("column", "A_Street");
            A_Street.addProperty("val", c_bp_bankAccount.getA_Street());

            SoapObject A_City = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            A_City.addAttribute("column", "A_City");
            A_City.addProperty("val", c_bp_bankAccount.getA_City());

            SoapObject A_Zip = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            A_Zip.addAttribute("column", "A_Zip");
            A_Zip.addProperty("val", c_bp_bankAccount.getA_Zip());

            SoapObject A_Country = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            A_Country.addAttribute("column", "A_Country");
            A_Country.addProperty("val", c_bp_bankAccount.getA_Country());

            SoapObject dataRow = new SoapObject(PropUtil.getProperty("nameSpace"), "DataRow");
            dataRow.addSoapObject(C_BPartner_ID);
            dataRow.addSoapObject(AccountNo);
            dataRow.addSoapObject(CreditCardType);
            dataRow.addSoapObject(CreditCardNumber);
            dataRow.addSoapObject(CreditCardVV);
            dataRow.addSoapObject(CreditCardExpMM);
            dataRow.addSoapObject(CreditCardExpYY);
            dataRow.addSoapObject(A_Name);
            dataRow.addSoapObject(A_Street);
            dataRow.addSoapObject(A_City);
            dataRow.addSoapObject(A_Zip);
            dataRow.addSoapObject(A_Country);

            SoapObject modelCRUD = new SoapObject(PropUtil.getProperty("nameSpace"), "ModelCRUD");
            modelCRUD.addProperty("serviceType", PropUtil.getProperty("bpBankAccountServiceType"));
            modelCRUD.addSoapObject(dataRow);

            SoapObject aDLoginRequest = ADLoginRequest.GetADLoginRequest(mUser, mPassword);

            SoapObject modelCRUDRequest = new SoapObject(PropUtil.getProperty("nameSpace"), "ModelCRUDRequest");
            modelCRUDRequest.addSoapObject(modelCRUD);
            modelCRUDRequest.addSoapObject(aDLoginRequest);

            SoapObject queryData = new SoapObject(PropUtil.getProperty("nameSpace"), "createUpdateData");
            queryData.addSoapObject(modelCRUDRequest);

            //request to server and get Soap Primitive response
            return parseXml(WebServiceCall.callWSThreadSoapPrimitive(queryData));

        } catch (Exception e) {
            LogUtil.logE(TAG, e.getMessage(), e);
            return 0;
        }
    }

    private static long parseXml(SoapObject soap){
        try{
            if (soap.getAttributeCount() == 1)
            {
                if (soap.getAttribute(0).toString() != null)
                    return Long.parseLong(soap.getAttribute(0).toString());
            }
            return 0;
        } catch (Exception e) {
            LogUtil.logE(TAG, e.getMessage(), e);
            return 0;
        }
    }
}
