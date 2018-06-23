package com.keredwell.scanandgo.webservice;

import com.keredwell.scanandgo.ApplicationContext;
import com.keredwell.scanandgo.data.AD_Org;
import com.keredwell.scanandgo.data.M_Pricelist;
import com.keredwell.scanandgo.data.M_Pricelist_Version;
import com.keredwell.scanandgo.dbhelper.AD_OrgDBAdapter;
import com.keredwell.scanandgo.dbhelper.M_PricelistDBAdapter;
import com.keredwell.scanandgo.dbhelper.M_Pricelist_VersionDBAdapter;
import com.keredwell.scanandgo.util.SharedPrefUtil;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class BranchScanWS {
    private static final String TAG = makeLogTag(C_BP_BankAccountWS.class);

    public static Boolean WSEvent(String branchCode) {
        String mUser = SharedPrefUtil.getPersistedData(ApplicationContext.USERNAME, null);
        String mPassword = SharedPrefUtil.getPersistedData(ApplicationContext.USERPASS, null);

        if (AD_OrgWS.WSEvent(branchCode, mUser, mPassword) == true) {
            AD_OrgDBAdapter ad_orgDBAdapter = new AD_OrgDBAdapter(ApplicationContext.getAppContext());
            AD_Org ad_org = ad_orgDBAdapter.getAD_Org(branchCode);
            if (ad_org != null) {
                SharedPrefUtil.setPersistedData(ApplicationContext.ADORGID, String.valueOf(ad_org.getAD_Org_ID()));

                if (M_PricelistWS.WSEvent(ad_org.getAD_Org_ID(), mUser, mPassword) == true) {
                    M_PricelistDBAdapter m_pricelistDBAdapter = new M_PricelistDBAdapter(ApplicationContext.getAppContext());
                    M_Pricelist m_pricelist = m_pricelistDBAdapter.getM_PricelistByAD_Org_Id(ad_org.getAD_Org_ID());
                    SharedPrefUtil.setPersistedData(ApplicationContext.MPRICELISTID, String.valueOf(m_pricelist.getM_Pricelist_ID()));
                    if (m_pricelist != null) {
                        if (M_Pricelist_VersionWS.WSEvent(m_pricelist.getM_Pricelist_ID(), mUser, mPassword) == true) {
                            M_Pricelist_VersionDBAdapter m_pricelist_versionDBAdapter = new M_Pricelist_VersionDBAdapter(ApplicationContext.getAppContext());
                            M_Pricelist_Version m_pricelist_version = m_pricelist_versionDBAdapter.getM_Pricelist_VersionByPricelist_ID(m_pricelist.getM_Pricelist_ID());
                            SharedPrefUtil.setPersistedData(ApplicationContext.MPRICELISTVERSIONID, String.valueOf(m_pricelist_version.getM_Pricelist_Version_ID()));
                            return true;
                        }
                        else
                            return false;
                    }
                    else
                        return false;
                }
                else
                    return false;
            }
            else
                return false;
        }
        else
            return false;
    }
}
