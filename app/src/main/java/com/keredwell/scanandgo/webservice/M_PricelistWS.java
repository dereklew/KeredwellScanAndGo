package com.keredwell.scanandgo.webservice;

import com.keredwell.scanandgo.ApplicationContext;
import com.keredwell.scanandgo.data.M_Pricelist;
import com.keredwell.scanandgo.dbhelper.M_PricelistDBAdapter;
import com.keredwell.scanandgo.util.DateUtil;
import com.keredwell.scanandgo.util.LogUtil;
import com.keredwell.scanandgo.util.PropUtil;

import org.ksoap2.serialization.SoapObject;

import java.util.Date;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

/**
 * Created by Derek on 25/11/2017.
 */

public class M_PricelistWS {
    private static final String TAG = makeLogTag(M_PricelistWS.class);

    public static Boolean WSEvent(long ad_org_id, String mUser, String mPassword)
    {
        try{
            SoapObject AD_Org_ID = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            AD_Org_ID.addAttribute("column", "AD_Org_ID");
            AD_Org_ID.addProperty("val", String.valueOf(ad_org_id));

            SoapObject dataRow = new SoapObject(PropUtil.getProperty("nameSpace"), "DataRow");
            dataRow.addSoapObject(AD_Org_ID);

            SoapObject modelCRUD = new SoapObject(PropUtil.getProperty("nameSpace"), "ModelCRUD");
            modelCRUD.addProperty("serviceType", PropUtil.getProperty("pricelistServiceType"));
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

                        M_Pricelist m_pricelist = new M_Pricelist();

                        m_pricelist.setM_Pricelist_ID(Long.parseLong(((SoapObject)((SoapObject)((SoapObject)soap.getProperty(0)).getProperty(i)).getProperty(0)).getProperty(0).toString()));
                        m_pricelist.setAd_Org_ID(Long.parseLong(((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(1)).getProperty(0).toString()));
                        m_pricelist.setName(((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(2)).getProperty(0).toString());

                        M_PricelistDBAdapter db = new M_PricelistDBAdapter(ApplicationContext.getAppContext());
                        if (db.getM_Pricelist(m_pricelist.getM_Pricelist_ID()) == null)
                        {
                            db.createM_Pricelist(m_pricelist);
                        }
                        else
                        {
                            db.updateM_Pricelist(m_pricelist);
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
