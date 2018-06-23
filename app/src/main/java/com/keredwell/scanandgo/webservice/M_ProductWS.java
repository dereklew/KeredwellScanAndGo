package com.keredwell.scanandgo.webservice;

import com.keredwell.scanandgo.ApplicationContext;
import com.keredwell.scanandgo.util.DateUtil;
import com.keredwell.scanandgo.data.M_Product;
import com.keredwell.scanandgo.dbhelper.M_ProductDBAdapter;
import com.keredwell.scanandgo.util.LogUtil;
import com.keredwell.scanandgo.util.PropUtil;

import org.ksoap2.serialization.SoapObject;

import java.util.Date;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

/**
 * Created by Derek on 25/11/2017.
 */

public class M_ProductWS {
    private static final String TAG = makeLogTag(M_ProductWS.class);

    public static Boolean WSEvent(String upc, String mUser, String mPassword)
    {
        try{
            SoapObject UPC = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            UPC.addAttribute("column", "UPC");
            UPC.addProperty("val", upc);

            SoapObject dataRow = new SoapObject(PropUtil.getProperty("nameSpace"), "DataRow");
            dataRow.addSoapObject(UPC);

            SoapObject modelCRUD = new SoapObject(PropUtil.getProperty("nameSpace"), "ModelCRUD");
            modelCRUD.addProperty("serviceType", PropUtil.getProperty("productServiceType"));
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

                        M_Product c_bpartner = new M_Product();

                        c_bpartner.setM_Product_ID(Long.parseLong(((SoapObject)((SoapObject)((SoapObject)soap.getProperty(0)).getProperty(i)).getProperty(0)).getProperty(0).toString()));
                        c_bpartner.setName(((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(1)).getProperty(0).toString());
                        if (((SoapObject)((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(2)).getProperty(0) != null) {
                            c_bpartner.setUPC(((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(2)).getProperty(0).toString());
                        }
                        if (((SoapObject)((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(3)).getProperty(0) != null) {
                            c_bpartner.setC_Uom_ID(Long.parseLong(((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(3)).getProperty(0).toString()));
                        }
                        if (((SoapObject)((SoapObject)((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(4)).getProperty(0) != null) {
                            c_bpartner.setM_Locator_ID(Long.parseLong(((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(4)).getProperty(0).toString()));
                        }

                        M_ProductDBAdapter db = new M_ProductDBAdapter(ApplicationContext.getAppContext());
                        if (db.getM_Product(c_bpartner.getM_Product_ID()) == null)
                        {
                            db.createM_Product(c_bpartner);
                        }
                        else
                        {
                            db.updateM_Product(c_bpartner);
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
