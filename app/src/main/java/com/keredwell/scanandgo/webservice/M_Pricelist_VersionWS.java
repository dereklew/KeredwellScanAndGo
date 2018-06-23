package com.keredwell.scanandgo.webservice;

import com.keredwell.scanandgo.ApplicationContext;
import com.keredwell.scanandgo.util.DateUtil;
import com.keredwell.scanandgo.data.M_Pricelist_Version;
import com.keredwell.scanandgo.dbhelper.M_Pricelist_VersionDBAdapter;
import com.keredwell.scanandgo.util.LogUtil;
import com.keredwell.scanandgo.util.PropUtil;

import org.ksoap2.serialization.SoapObject;

import java.util.Date;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

/**
 * Created by Derek on 25/11/2017.
 */

public class M_Pricelist_VersionWS {
    private static final String TAG = makeLogTag(M_Pricelist_VersionWS.class);

    public static Boolean WSEvent(long m_pricelist_version_id, String mUser, String mPassword)
    {
        try{
            SoapObject M_Pricelist_Version_ID = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            M_Pricelist_Version_ID.addAttribute("column", "M_Pricelist_Version_ID");
            M_Pricelist_Version_ID.addProperty("val", String.valueOf(m_pricelist_version_id));

            SoapObject dataRow = new SoapObject(PropUtil.getProperty("nameSpace"), "DataRow");
            dataRow.addSoapObject(M_Pricelist_Version_ID);

            SoapObject modelCRUD = new SoapObject(PropUtil.getProperty("nameSpace"), "ModelCRUD");
            modelCRUD.addProperty("serviceType", PropUtil.getProperty("pricelistVersionServiceType"));
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

                        M_Pricelist_Version m_pricelist_version = new M_Pricelist_Version();

                        m_pricelist_version.setM_Pricelist_Version_ID(Long.parseLong(((SoapObject)((SoapObject)((SoapObject)soap.getProperty(0)).getProperty(i)).getProperty(0)).getProperty(0).toString()));
                        m_pricelist_version.setM_Pricelist_ID(Long.parseLong(((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(1)).getProperty(0).toString()));

                        M_Pricelist_VersionDBAdapter db = new M_Pricelist_VersionDBAdapter(ApplicationContext.getAppContext());
                        if (db.getM_Pricelist_Version(m_pricelist_version.getM_Pricelist_Version_ID()) == null)
                        {
                            db.createM_Pricelist_Version(m_pricelist_version);
                        }
                        else
                        {
                            db.updateM_Pricelist_Version(m_pricelist_version);
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
