package com.keredwell.scanandgo.webservice;

import com.keredwell.scanandgo.ApplicationContext;
import com.keredwell.scanandgo.data.M_Product;
import com.keredwell.scanandgo.dbhelper.M_ProductDBAdapter;
import com.keredwell.scanandgo.util.SharedPrefUtil;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class ItemScanWS {
    private static final String TAG = makeLogTag(ItemScanWS.class);

    public static Boolean WSEvent(String itemCode) {
        String mUser = SharedPrefUtil.getPersistedData(ApplicationContext.USERNAME, null);
        String mPassword = SharedPrefUtil.getPersistedData(ApplicationContext.USERPASS, null);

        if (M_ProductWS.WSEvent(itemCode, mUser, mPassword) == true) {
            M_ProductDBAdapter m_productDBAdapter = new M_ProductDBAdapter(ApplicationContext.getAppContext());
            M_Product product = m_productDBAdapter.getM_ProductByUPC(itemCode);

            if (M_ProductPriceWS.WSEvent(product.getM_Product_ID(), Long.parseLong(SharedPrefUtil.getPersistedData(ApplicationContext.MPRICELISTVERSIONID, null)),
                    mUser, mPassword) == true) {

                if (M_LocatorWS.WSEvent(product.getM_Locator_ID(), mUser, mPassword))
                    return true;
            }
        }
        return false;
    }
}
