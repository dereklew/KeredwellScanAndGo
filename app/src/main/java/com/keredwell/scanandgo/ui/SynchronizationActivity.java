package com.keredwell.scanandgo.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.keredwell.scanandgo.ApplicationContext;
import com.keredwell.scanandgo.R;
import com.keredwell.scanandgo.util.DateUtil;
import com.keredwell.scanandgo.ui.base.BaseActivity;
import com.keredwell.scanandgo.ui.order.OrderListActivity;
import com.keredwell.scanandgo.util.LogUtil;
import com.keredwell.scanandgo.util.PropUtil;
import com.keredwell.scanandgo.util.SharedPrefUtil;
import com.keredwell.scanandgo.webservice.C_BP_GroupWS;
import com.keredwell.scanandgo.webservice.C_BPartnerWS;
import com.keredwell.scanandgo.webservice.C_BPartner_LocationWS;
import com.keredwell.scanandgo.webservice.C_LocationWS;
import com.keredwell.scanandgo.webservice.C_OrderLineReceiveWS;
import com.keredwell.scanandgo.webservice.C_OrderReceiveWS;
import com.keredwell.scanandgo.webservice.C_TaxWS;
import com.keredwell.scanandgo.webservice.M_LocatorWS;
import com.keredwell.scanandgo.webservice.M_Pricelist_VersionWS;
import com.keredwell.scanandgo.webservice.M_ProductPriceWS;
import com.keredwell.scanandgo.webservice.M_ProductWS;
import com.keredwell.scanandgo.webservice.M_Product_CategoryWS;
import com.keredwell.scanandgo.webservice.SendOrders;

import java.util.Date;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

/**
 * This Activity provides several settings. Activity contains {@link PreferenceFragment} as inner class.
 *
 * Created by Andreas Schrade on 14.12.2015.
 */
public class SynchronizationActivity extends BaseActivity {
    private static final String TAG = makeLogTag(SynchronizationActivity.class);

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private SyncTask mSyncTask = null;

    private View mProgressView;
    private View mSyncFormView;
    private View mFabView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronization);
        setupToolbar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSync();
            }
        });

        mProgressView = findViewById(R.id.sync_progress);
        mSyncFormView = findViewById(R.id.sync_main);
        mFabView = findViewById(R.id.sync_fab);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSync() {
        if (mSyncTask != null) {
            return;
        }

        // Show a progress spinner, and kick off a background task to
        // perform the synchronization attempt.
        showProgress(true);
        mSyncTask = new SynchronizationActivity.SyncTask();
        mSyncTask.execute((Void) null);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSyncFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSyncFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSyncFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mFabView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFabView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFabView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSyncFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFabView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class SyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean ret = false;
            try{
                String mUser = SharedPrefUtil.getPersistedData(ApplicationContext.USERNAME, null);
                String mPassword = SharedPrefUtil.getPersistedData(ApplicationContext.USERPASS, null);
                String mUserID = SharedPrefUtil.getPersistedData(ApplicationContext.USERID, null);

                Date lastUpdatedDate = DateUtil.ConvertToDate(PropUtil.getProperty("lastUpdatedatetime"));
                if (C_BP_GroupWS.WSEvent(mUser, mPassword, lastUpdatedDate) == false)
                    return false;

                if (C_BPartner_LocationWS.WSEvent(mUser, mPassword, lastUpdatedDate) == false)
                    return false;

                if (C_BPartnerWS.WSEvent(mUser, mPassword, lastUpdatedDate) == false)
                    return false;

                if (C_LocationWS.WSEvent(mUser, mPassword, lastUpdatedDate) == false)
                    return false;

                if (M_Pricelist_VersionWS.WSEvent(mUser, mPassword, lastUpdatedDate) == false)
                    return false;

                if (M_Product_CategoryWS.WSEvent(mUser, mPassword, lastUpdatedDate) == false)
                    return false;

                if (M_ProductPriceWS.WSEvent(mUser, mPassword, lastUpdatedDate) == false)
                    return false;

                if (M_ProductWS.WSEvent(mUser, mPassword, lastUpdatedDate) == false)
                    return false;

                if (M_LocatorWS.WSEvent(mUser, mPassword, lastUpdatedDate) == false)
                    return false;

                if (C_TaxWS.WSEvent(mUser, mPassword, lastUpdatedDate) == false)
                    return false;

                if (SendOrders.sendXml(mUser, mPassword) == false)
                    return false;

                if (C_OrderReceiveWS.WSEvent(mUser, mPassword, mUserID, lastUpdatedDate) == false)
                    return false;

                if (C_OrderLineReceiveWS.WSEvent(mUser, mPassword, mUserID, lastUpdatedDate) == false)
                    return false;

                PropUtil.setProperty("lastUpdatedatetime", DateUtil.ConvertToString(new Date()));
            } catch (Exception e) {
                LogUtil.logE(TAG, e.getMessage(), e);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSyncTask = null;
            showProgress(false);
            mSyncFormView.requestFocus();

            AlertDialog alertDialog = new AlertDialog.Builder(SynchronizationActivity.this).create();
            alertDialog.setTitle(R.string.synchronization_title);

            if (success) {
                alertDialog.setMessage(getApplicationContext().getString(R.string.succeeded));
            } else {
                alertDialog.setMessage(getApplicationContext().getString(R.string.synchronization_failed));
            }
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getApplicationContext().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (success) {
                                Intent intent = new Intent(SynchronizationActivity.this, OrderListActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
            alertDialog.show();
        }

        @Override
        protected void onCancelled() {
            mSyncFormView = null;
            showProgress(false);
        }
    }

    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                openDrawer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return R.id.nav_synchronization;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }
}
