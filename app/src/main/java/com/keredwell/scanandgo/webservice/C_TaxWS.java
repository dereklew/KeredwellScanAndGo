package com.keredwell.scanandgo.webservice;

import com.keredwell.scanandgo.ApplicationContext;
import com.keredwell.scanandgo.data.C_Tax;
import com.keredwell.scanandgo.util.DateUtil;
import com.keredwell.scanandgo.dbhelper.C_TaxDBAdapter;
import com.keredwell.scanandgo.util.LogUtil;
import com.keredwell.scanandgo.util.PropUtil;

import org.ksoap2.serialization.SoapObject;

import java.util.Date;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

/**
 * Created by Derek on 25/11/2017.
 */

public class C_TaxWS {
    private static final String TAG = makeLogTag(C_TaxWS.class);

    public static Boolean WSEvent(String mUser, String mPassword, Date lastUpdatedDate)
    {
        try{
            SoapObject field = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            field.addAttribute("column", "UpdatedDateTime");
            field.addProperty("val", DateUtil.ConvertToString(lastUpdatedDate));

            SoapObject dataRow = new SoapObject(PropUtil.getProperty("nameSpace"), "DataRow");
            dataRow.addSoapObject(field);

            SoapObject modelCRUD = new SoapObject(PropUtil.getProperty("nameSpace"), "ModelCRUD");
            modelCRUD.addProperty("serviceType", PropUtil.getProperty("taxServiceType"));
            modelCRUD.addSoapObject(dataRow);

            SoapObject aDLoginRequest = ADLoginRequest.GetADLoginRequest(mUser, mPassword);

            SoapObject modelCRUDRequest = new SoapObject(PropUtil.getProperty("nameSpace"), "ModelCRUDRequest");
            modelCRUDRequest.addSoapObject(modelCRUD);
            modelCRUDRequest.addSoapObject(aDLoginRequest);

            SoapObject queryData = new SoapObject(PropUtil.getProperty("nameSpace"), "queryData");
            queryData.addSoapObject(modelCRUDRequest);

            //request to server and get Soap Primitive response
            return parseXml(WebServiceCall.callWSThreadSoapPrimitive(queryData));

        } catch (Exception e) {
            LogUtil.logE(TAG, e.getMessage(), e);
            return false;
        }
    }

    private static Boolean parseXml(SoapObject soap){
        try{
            if (soap.getPropertyCount() == 3)
            {
                if (soap.getProperty(2).toString().equals("true")) {
                    for(int i=0; i<Integer.parseInt(soap.getProperty(1).toString()); i++) {

                        C_Tax c_tax = new C_Tax();

                        c_tax.setC_Tax_ID(Long.parseLong(((SoapObject)((SoapObject)((SoapObject)soap.getProperty(0)).getProperty(i)).getProperty(0)).getProperty(0).toString()));
                        c_tax.setName(((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(1)).getProperty(0).toString());
                        c_tax.setRate((int)(Double.parseDouble(((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(2)).getProperty(0).toString())*100));

                        C_TaxDBAdapter db = new C_TaxDBAdapter(ApplicationContext.getAppContext());
                        if (db.getC_Tax(c_tax.getC_Tax_ID()) == null)
                        {
                            db.createC_Tax(c_tax);
                        }
                        else
                        {
                            db.updateC_Tax(c_tax);
                        }
                    }
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            LogUtil.logE(TAG, e.getMessage(), e);
            return false;
        }
    }
}
