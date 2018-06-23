package com.keredwell.scanandgo.webservice;

import com.keredwell.scanandgo.ApplicationContext;
import com.keredwell.scanandgo.data.C_BPartner;
import com.keredwell.scanandgo.util.DateUtil;
import com.keredwell.scanandgo.dbhelper.C_BPartnerDBAdapter;
import com.keredwell.scanandgo.util.LogUtil;
import com.keredwell.scanandgo.util.PropUtil;

import org.ksoap2.serialization.SoapObject;

import java.util.Date;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class C_BPartnerWS {
    private static final String TAG = makeLogTag(C_BPartnerWS.class);

    public static Boolean WSEvent(String mUser, String mPassword, Date lastUpdatedDate)
    {
        try{
            SoapObject field = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            field.addAttribute("column", "UpdatedDateTime");
            field.addProperty("val", DateUtil.ConvertToString(lastUpdatedDate));

            SoapObject dataRow = new SoapObject(PropUtil.getProperty("nameSpace"), "DataRow");
            dataRow.addSoapObject(field);

            SoapObject modelCRUD = new SoapObject(PropUtil.getProperty("nameSpace"), "ModelCRUD");
            modelCRUD.addProperty("serviceType", PropUtil.getProperty("bpartnerServiceType"));
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
                    C_BPartnerDBAdapter db = new C_BPartnerDBAdapter(ApplicationContext.getAppContext());
                    for(int i=0; i<Integer.parseInt(soap.getProperty(1).toString()); i++) {

                        C_BPartner c_bpartner = new C_BPartner();

                        c_bpartner.setC_BPartner_ID(Long.parseLong(((SoapObject)((SoapObject)((SoapObject)soap.getProperty(0)).getProperty(i)).getProperty(0)).getProperty(0).toString()));
                        c_bpartner.setName(((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(1)).getProperty(0).toString());

                        if (db.getC_BPartner(c_bpartner.getC_BPartner_ID()) == null)
                        {
                            db.createC_BPartner(c_bpartner);
                        }
                        else
                        {
                            db.updateC_BPartner(c_bpartner);
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
