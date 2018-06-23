package com.keredwell.scanandgo;

import android.app.Application;
import android.content.Context;

import rego.printlib.export.regoPrinter;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class ApplicationContext extends Application {
    private static final String TAG = makeLogTag(ApplicationContext.class);

    public static final String USERID = "UserId";
    public static final String USERNAME = "UserName";
    public static final String USERPASS = "UserPass";
    public static final String ADORGID = "ADOrgID";
    public static final String MPRICELISTID = "PricelistID";
    public static final String MPRICELISTVERSIONID = "PricelistVersionID";

    private static Context context;

    public void onCreate() {
        super.onCreate();
        ApplicationContext.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ApplicationContext.context;
    }
}