package com.keredwell.scanandgo.webservice;

import com.keredwell.scanandgo.ApplicationContext;
import com.keredwell.scanandgo.util.DateUtil;
import com.keredwell.scanandgo.data.M_ProductPrice;
import com.keredwell.scanandgo.dbhelper.M_ProductPriceDBAdapter;
import com.keredwell.scanandgo.util.LogUtil;
import com.keredwell.scanandgo.util.PropUtil;

import org.ksoap2.serialization.SoapObject;

import java.util.Date;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

/**
 * Created by Derek on 25/11/2017.
 */

public class M_ProductPriceWS {
    private static final String TAG = makeLogTag(M_ProductPriceWS.class);

    public static Boolean WSEvent(long m_product_id, long m_pricelist_version_id, String mUser, String mPassword)
    {
        try{
            SoapObject M_Product_Id = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            M_Product_Id.addAttribute("column", "M_Product_Id");
            M_Product_Id.addProperty("val", String.valueOf(m_product_id));

            SoapObject M_Pricelist_Version_Id = new SoapObject(PropUtil.getProperty("nameSpace"), "field");
            M_Pricelist_Version_Id.addAttribute("column", "M_Pricelist_Version_Id");
            M_Pricelist_Version_Id.addProperty("val", String.valueOf(m_pricelist_version_id));

            SoapObject dataRow = new SoapObject(PropUtil.getProperty("nameSpace"), "DataRow");
            dataRow.addSoapObject(M_Product_Id);
            dataRow.addSoapObject(M_Pricelist_Version_Id);

            SoapObject modelCRUD = new SoapObject(PropUtil.getProperty("nameSpace"), "ModelCRUD");
            modelCRUD.addProperty("serviceType", PropUtil.getProperty("productPriceServiceType"));
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

                        M_ProductPrice m_productPrice = new M_ProductPrice();

                        m_productPrice.setM_Product_ID(Long.parseLong(((SoapObject)((SoapObject)((SoapObject)soap.getProperty(0)).getProperty(i)).getProperty(0)).getProperty(0).toString()));
                        m_productPrice.setM_Pricelist_Version_ID(Long.parseLong(((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(1)).getProperty(0).toString()));
                        m_productPrice.setPriceList((int)(Double.parseDouble(((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(2)).getProperty(0).toString())*100));
                        m_productPrice.setPriceStd((int)(Double.parseDouble(((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(3)).getProperty(0).toString())*100));
                        m_productPrice.setPriceLimit((int)(Double.parseDouble(((SoapObject) ((SoapObject) ((SoapObject) soap.getProperty(0)).getProperty(i)).getProperty(4)).getProperty(0).toString())*100));

                        M_ProductPriceDBAdapter db = new M_ProductPriceDBAdapter(ApplicationContext.getAppContext());
                        if (db.getM_ProductPrice(m_productPrice.getM_Product_ID()) == null)
                        {
                            db.createM_ProductPrice(m_productPrice);
                        }
                        else
                        {
                            db.updateM_ProductPrice(m_productPrice);
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
