package com.keredwell.scanandgo.ui.scanner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.keredwell.scanandgo.R;
import com.keredwell.scanandgo.data.C_BPartner;
import com.keredwell.scanandgo.data.C_Order;
import com.keredwell.scanandgo.data.C_OrderLine;
import com.keredwell.scanandgo.data.C_Tax;
import com.keredwell.scanandgo.dbhelper.C_OrderDBAdapter;
import com.keredwell.scanandgo.dbhelper.C_OrderLineDBAdapter;
import com.keredwell.scanandgo.ui.base.BaseActivity;
import com.keredwell.scanandgo.ui.orderlisting.OrderListPageAdapter;
import com.keredwell.scanandgo.util.LogUtil;
import com.keredwell.scanandgo.util.SharedPrefUtil;
import com.keredwell.scanandgo.webservice.SendOrders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class CheckoutActivity extends BaseActivity implements CheckoutFragment.Callback {
    private static final String TAG = makeLogTag(CheckoutActivity.class);

    /**
     * Keep track of the order sync task to ensure we can cancel it if requested.
     */
    private CheckoutActivity.OrderSyncTask mOrderSysncTask = null;
    private View mProgressView;
    private View mOrderFormView;

    private C_Order c_order = new C_Order();
    private ArrayList<C_OrderLine> c_orderItems = new ArrayList<>();
    private Boolean isCash = false;
    private C_Tax c_tax = new C_Tax();
    private C_BPartner c_bPartner = new C_BPartner();

    private C_OrderDBAdapter c_orderDBAdapter;
    private C_OrderLineDBAdapter c_orderLineDBAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        c_orderDBAdapter = new C_OrderDBAdapter(this);
        c_orderLineDBAdapter = new C_OrderLineDBAdapter(this);

        c_order = (C_Order)getIntent().getSerializableExtra("c_order");
        c_orderItems = (ArrayList<C_OrderLine>)getIntent().getSerializableExtra("c_orderItems");
        isCash = (Boolean) getIntent().getSerializableExtra("isCash");
        c_tax = (C_Tax)getIntent().getSerializableExtra("mTax");
        c_bPartner = (C_BPartner)getIntent().getSerializableExtra("customer");

        setContentView(R.layout.activity_checkoutscan);

        setupToolbar();

        updateTotalView();

        mOrderFormView = findViewById(R.id.order_main);
        mProgressView = findViewById(R.id.order_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSync() {
        if (mOrderSysncTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mOrderSysncTask = new CheckoutActivity.OrderSyncTask();
            mOrderSysncTask.execute((Void) null);
        }
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

            mOrderFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mOrderFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mOrderFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mOrderFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class OrderSyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String mUser = SharedPrefUtil.getPersistedData("UserName", null);
                String mPassword = SharedPrefUtil.getPersistedData("UserPass", null);

                c_order.setInternal_Status(OrderListPageAdapter.FRAG_COMPLETED);
                long orderid;
                if (c_order.getID() > 0) {
                    c_orderDBAdapter.updateC_Order(c_order);
                    orderid = c_order.getID();
                }
                else {
                    orderid = c_orderDBAdapter.createC_Order(c_order);
                }
                Iterator<C_OrderLine> orderItemIterator = c_orderItems.iterator();
                while (orderItemIterator.hasNext()) {
                    C_OrderLine nextOrderItem = orderItemIterator.next();
                    nextOrderItem.setOrderID(orderid);
                    if (nextOrderItem.getID() > 0) {
                        c_orderLineDBAdapter.updateC_OrderLine(nextOrderItem);
                    } else {
                        c_orderLineDBAdapter.createC_OrderLine(nextOrderItem);
                    }
                }

                return SendOrders.sendXml(mUser, mPassword);

            } catch (Exception e) {
                LogUtil.logE(TAG, e.getMessage(), e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mOrderSysncTask = null;
            showProgress(false);

            if (!success) {
                AlertDialog alertDialog = new AlertDialog.Builder(CheckoutActivity.this).create();
                alertDialog.setTitle(R.string.synchronization_title);
                alertDialog.setMessage(getApplicationContext().getString(R.string.synchronization_failed_sync));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getApplicationContext().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(CheckoutActivity.this, OrderListActivity.class);
                                startActivity(intent);
                            }
                        });
                alertDialog.show();
            }
        }

        @Override
        protected void onCancelled() {
            mOrderSysncTask = null;
            showProgress(false);
        }
    }

    private void updateTotalView()
    {
        TextView vCustomerName = (TextView) findViewById(R.id.customername);
        TextView vCustomerAddress = (TextView) findViewById(R.id.customeraddress);
        TextView vCustomerPostal = (TextView) findViewById(R.id.customerpostal);

        vCustomerName.setText(c_bPartner.getName());
        vCustomerAddress.setText(c_bPartner.getAddress());
        vCustomerPostal.setText(c_bPartner.getPostal());

        TextView vSubtotal = (TextView) findViewById(R.id.subtotal);
        TextView vTax = (TextView) findViewById(R.id.tax);
        TextView vTotal = (TextView) findViewById(R.id.total);

        vSubtotal.setText(String.format( "%.2f", BigDecimal.valueOf(c_order.getTotalLines()).movePointLeft(2)));
        vTax.setText(String.format( "%.2f", BigDecimal.valueOf(c_order.getGrandTotal() -  c_order.getTotalLines()).movePointLeft(2)));
        vTotal.setText(String.format( "%.2f", BigDecimal.valueOf(c_order.getGrandTotal()).movePointLeft(2)));
    }

    public ArrayList<C_OrderLine> getListOrderItems()
    {
        return this.c_orderItems;
    }

    /**
     * Called when an item has been selected
     *
     * @param id the selected quote ID
     */
    @Override
    public void onItemSelected(int id) {
        /*Customer customer = customers.get(id);
        if (twoPaneMode) {
            // Show the detail information by replacing the DetailFragment via transaction.

            Bundle arguments = new Bundle();
            arguments.putLong(CustomerDetailFragment.ARG_ITEM_ID, customer.getID());
            arguments.putString(CustomerDetailFragment.ARG_ITEM_NAME, customer.getName());
            arguments.putString(CustomerDetailFragment.ARG_ITEM_ADDRESS, customer.getAddress());
            arguments.putString(CustomerDetailFragment.ARG_ITEM_TEL, customer.getTel());

            CustomerDetailFragment fragment = CustomerDetailFragment.newInstance(arguments);
            getFragmentManager().beginTransaction().replace(R.id.article_detail_container, fragment).commit();
        } else {
            // Start the detail activity in single pane mode.

            Bundle arguments = new Bundle();
            arguments.putLong(CustomerDetailFragment.ARG_ITEM_ID, customer.getID());
            arguments.putString(CustomerDetailFragment.ARG_ITEM_NAME, customer.getName());
            arguments.putString(CustomerDetailFragment.ARG_ITEM_ADDRESS, customer.getAddress());
            arguments.putString(CustomerDetailFragment.ARG_ITEM_TEL, customer.getTel());

            Intent detailIntent = new Intent(this, CustomerDetailActivity.class);
            detailIntent.putExtras(arguments);
            startActivity(detailIntent);
        }*/
    }

    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Enables the functionality that selected items are automatically highlighted.
     */
    private void enableActiveItemState() {
        CheckoutFragment fragmentById = (CheckoutFragment) getFragmentManager().findFragmentById(R.id.article_list);
        fragmentById.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                openDrawer();
                return true;
            case R.id.action_send:
                attemptSync();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return R.id.nav_order;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }
}
