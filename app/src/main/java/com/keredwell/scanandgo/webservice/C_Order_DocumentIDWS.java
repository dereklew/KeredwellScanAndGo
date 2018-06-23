package com.keredwell.scanandgo.webservice;

import com.keredwell.scanandgo.ApplicationContext;
import com.keredwell.scanandgo.data.C_Order;
import com.keredwell.scanandgo.dbhelper.C_BPartnerDBAdapter;
import com.keredwell.scanandgo.dbhelper.C_OrderDBAdapter;
import com.keredwell.scanandgo.util.LogUtil;
import com.keredwell.scanandgo.util.PropUtil;

import org.ksoap2.serialization.SoapObject;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class C_Order_DocumentIDWS {
    private static final String TAG = makeLogTag(C_Order_DocumentIDWS.class);

    public static Boolean WSEvent(String mUser, String mPassword, long c_order_id)
    {
        try{
            SoapObject modelCRUD = new SoapObject(PropUtil.getProperty("nameSpace"), "ModelCRUD");
            modelCRUD.addProperty("serviceType", PropUtil.getProperty("orderDocIDServiceType"));
            modelCRUD.addProperty("RecordID", c_order_id);

            SoapObject aDLoginRequest = ADLoginRequest.GetADLoginRequest(mUser, mPassword);

            SoapObject modelCRUDRequest = new SoapObject(PropUtil.getProperty("nameSpace"), "ModelCRUDRequest");
            modelCRUDRequest.addSoapObject(modelCRUD);
            modelCRUDRequest.addSoapObject(aDLoginRequest);

            SoapObject queryData = new SoapObject(PropUtil.getProperty("nameSpace"), "readData");
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
                    C_OrderDBAdapter c_orderDBAdapter = new C_OrderDBAdapter(ApplicationContext.getAppContext());
                    C_BPartnerDBAdapter c_bPartnerDBAdapter = new C_BPartnerDBAdapter(ApplicationContext.getAppContext());
                    for(int i=0; i<Integer.parseInt(soap.getProperty(1).toString()); i++) {

                        C_Order c_order = c_orderDBAdapter.getC_OrderByC_Order_ID(Long.parseLong(((SoapObject)((SoapObject)((SoapObject)soap.getProperty(0)).getProperty(i)).getProperty(0)).getProperty(0).toString()));
                        if (c_order != null) {
                            c_order.setDocumentID(Long.parseLong(((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(1)).getProperty(0).toString()));
                            c_orderDBAdapter.updateC_OrderWithC_Order_ID(c_order);
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
