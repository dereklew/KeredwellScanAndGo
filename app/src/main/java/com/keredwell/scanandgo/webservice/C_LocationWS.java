package com.keredwell.scanandgo.webservice;

import com.keredwell.scanandgo.ApplicationContext;
import com.keredwell.scanandgo.data.C_Location;
import com.keredwell.scanandgo.util.DateUtil;
import com.keredwell.scanandgo.dbhelper.C_LocationDBAdapter;
import com.keredwell.scanandgo.util.LogUtil;
import com.keredwell.scanandgo.util.PropUtil;

import org.ksoap2.serialization.SoapObject;

import java.util.Date;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class C_LocationWS {
    private static final String TAG = makeLogTag(C_LocationWS.class);

    public static Boolean WSEvent(String mUser, String mPassword, Date lastUpdatedDate)
    {
        try{
            SoapObject field = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            field.addAttribute("column", "UpdatedDateTime");
            field.addProperty("val", DateUtil.ConvertToString(lastUpdatedDate));

            SoapObject dataRow = new SoapObject(PropUtil.getProperty("nameSpace"), "DataRow");
            dataRow.addSoapObject(field);

            SoapObject modelCRUD = new SoapObject(PropUtil.getProperty("nameSpace"), "ModelCRUD");
            modelCRUD.addProperty("serviceType", PropUtil.getProperty("locationServiceType"));
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
                    C_LocationDBAdapter db = new C_LocationDBAdapter(ApplicationContext.getAppContext());
                    for(int i=0; i<Integer.parseInt(soap.getProperty(1).toString()); i++) {

                        C_Location c_location = new C_Location();

                        c_location.setC_Location_ID(Long.parseLong(((SoapObject)((SoapObject)((SoapObject)soap.getProperty(0)).getProperty(i)).getProperty(0)).getProperty(0).toString()));
                        String address = "";
                        if (((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(1)).getProperty(0) != null) {
                            address = (((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(1)).getProperty(0).toString());
                        }

                        if (((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(2)).getProperty(0) != null) {
                            address += (((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(2)).getProperty(0).toString());
                        }

                        c_location.setAddress(address);

                        if (((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(3)).getProperty(0) != null) {
                            c_location.setPostal(((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(3)).getProperty(0).toString());
                        }

                        if (db.getC_Location(c_location.getC_Location_ID()) == null)
                        {
                            db.createC_Location(c_location);
                        }
                        else
                        {
                            db.updateC_Location(c_location);
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
