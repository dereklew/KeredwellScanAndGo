package com.keredwell.scanandgo.webservice;

import com.keredwell.scanandgo.ApplicationContext;
import com.keredwell.scanandgo.data.AD_Org;
import com.keredwell.scanandgo.data.C_BPartner;
import com.keredwell.scanandgo.dbhelper.AD_OrgDBAdapter;
import com.keredwell.scanandgo.dbhelper.C_BPartnerDBAdapter;
import com.keredwell.scanandgo.util.DateUtil;
import com.keredwell.scanandgo.util.LogUtil;
import com.keredwell.scanandgo.util.PropUtil;

import org.ksoap2.serialization.SoapObject;

import java.util.Date;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class AD_OrgWS {
    private static final String TAG = makeLogTag(AD_OrgWS.class);

    public static Boolean WSEvent(String value, String mUser, String mPassword)
    {
        try{
            SoapObject Value = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            Value.addAttribute("column", "Value");
            Value.addProperty("val", value);

            SoapObject dataRow = new SoapObject(PropUtil.getProperty("nameSpace"), "DataRow");
            dataRow.addSoapObject(Value);

            SoapObject modelCRUD = new SoapObject(PropUtil.getProperty("nameSpace"), "ModelCRUD");
            modelCRUD.addProperty("serviceType", PropUtil.getProperty("adOrgServiceType"));
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
                    AD_OrgDBAdapter db = new AD_OrgDBAdapter(ApplicationContext.getAppContext());
                    for(int i=0; i<Integer.parseInt(soap.getProperty(1).toString()); i++) {

                        AD_Org ad_org = new AD_Org();

                        ad_org.setAD_Org_ID(Long.parseLong(((SoapObject)((SoapObject)((SoapObject)soap.getProperty(0)).getProperty(i)).getProperty(0)).getProperty(0).toString()));
                        ad_org.setValue(((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(1)).getProperty(0).toString());
                        ad_org.setName(((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(2)).getProperty(0).toString());

                        if (db.getAD_Org(ad_org.getAD_Org_ID()) == null)
                        {
                            db.createAD_Org(ad_org);
                        }
                        else
                        {
                            db.updateAD_Org(ad_org);
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
