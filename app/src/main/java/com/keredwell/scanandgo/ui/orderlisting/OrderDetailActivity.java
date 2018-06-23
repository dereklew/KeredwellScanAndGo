package com.keredwell.scanandgo.ui.orderlisting;

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
import com.keredwell.scanandgo.dbhelper.C_BPartnerDBAdapter;
import com.keredwell.scanandgo.dbhelper.C_OrderLineDBAdapter;
import com.keredwell.scanandgo.dbhelper.C_TaxDBAdapter;
import com.keredwell.scanandgo.ui.base.BaseActivity;
import com.keredwell.scanandgo.util.LogUtil;

import java.math.BigDecimal;
import java.util.ArrayList;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class OrderDetailActivity extends BaseActivity implements OrderDetailFragment.Callback {
    private static final String TAG = makeLogTag(OrderDetailActivity.class);

    /**
     * Keep track of the order sync task to ensure we can cancel it if requested.
     */
    private C_OrderLineDBAdapter orderLineDBAdapter;
    private C_TaxDBAdapter taxDBAdapter;
    private C_BPartnerDBAdapter bPartnerDBAdapter;

    private ArrayList<C_Order> mItem = new ArrayList<>();
    private int position = 0;
    private C_Order c_order = new C_Order();
    private ArrayList<C_OrderLine> c_orderItems = new ArrayList<>();
    private Boolean isCash = false;
    private C_Tax c_tax = new C_Tax();
    private C_BPartner c_bPartner = new C_BPartner();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        orderLineDBAdapter = new C_OrderLineDBAdapter(this);
        taxDBAdapter = new C_TaxDBAdapter(this);
        bPartnerDBAdapter = new C_BPartnerDBAdapter(this);

        mItem = (ArrayList<C_Order>)getIntent().getSerializableExtra("mItem");
        position = (int)getIntent().getSerializableExtra("position");

        setContentView(R.layout.activity_orderlisting_detail);

        setupToolbar();

        updateTotalView();
    }

    private void refreshData(){
        c_order = mItem.get(position);
        c_orderItems = orderLineDBAdapter.getAllC_OrderLinesByOrderId(mItem.get(position).getID());
        isCash = mItem.get(position).getIsCash();
        c_tax = taxDBAdapter.getC_Tax(c_orderItems.get(0).getC_Tax_ID());
        c_bPartner = bPartnerDBAdapter.getC_BPartner(mItem.get(position).getC_BPartner_ID());
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
        refreshData();

        return this.c_orderItems;
    }

    /**
     * Called when an item has been selected
     *
     * @param id the selected quote ID
     */
    @Override
    public void onItemSelected(int id) {

    }

    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        return R.id.nav_history;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }
}
