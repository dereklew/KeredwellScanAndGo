package com.keredwell.scanandgo.ui.customer;

import android.os.Bundle;

import com.keredwell.scanandgo.ui.base.BaseActivity;
import com.keredwell.scanandgo.R;
import com.keredwell.scanandgo.util.LogUtil;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class CustomerDetailActivity extends BaseActivity {
    private static final String TAG = makeLogTag(CustomerDetailActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_customers_detail);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            Bundle arguments = new Bundle();
            arguments = getIntent().getExtras();

            CustomerDetailFragment fragment =  CustomerDetailFragment.newInstance(arguments);
            getFragmentManager().beginTransaction().replace(R.id.article_detail_container, fragment).commit();
        } catch (Exception e) {
            LogUtil.logE(TAG, e.getMessage(), e);
        }
    }

    @Override
    public boolean providesActivityToolbar() {
        return false;
    }
}