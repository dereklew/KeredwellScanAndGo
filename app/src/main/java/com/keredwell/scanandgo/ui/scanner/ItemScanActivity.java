package com.keredwell.scanandgo.ui.scanner;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.keredwell.scanandgo.ApplicationContext;
import com.keredwell.scanandgo.R;

import com.keredwell.scanandgo.data.C_BPartner;
import com.keredwell.scanandgo.data.C_Order;
import com.keredwell.scanandgo.data.C_OrderLine;
import com.keredwell.scanandgo.data.C_Tax;
import com.keredwell.scanandgo.data.M_Product;
import com.keredwell.scanandgo.dbhelper.M_LocatorDBAdapter;
import com.keredwell.scanandgo.dbhelper.M_ProductDBAdapter;
import com.keredwell.scanandgo.ui.base.BaseActivity;
import com.keredwell.scanandgo.util.PropUtil;
import com.keredwell.scanandgo.util.SharedPrefUtil;
import com.keredwell.scanandgo.webservice.ItemScanWS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class ItemScanActivity extends BaseActivity implements ItemScanFragment.Callback {
    private static final String TAG = makeLogTag(ItemScanActivity.class);

    private C_Order c_order = new C_Order();
    private ArrayList<C_OrderLine> c_orderLines = new ArrayList<>();
    private C_Tax c_tax = new C_Tax();
    private C_BPartner c_bPartner = new C_BPartner();
    private boolean isCash;

    private M_LocatorDBAdapter m_locatorDBAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_locatorDBAdapter = new M_LocatorDBAdapter(this);

        setContentView(R.layout.activity_itemscan);
        setupToolbar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemScanActivity.this, SimpleScannerActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent in ) {
        if( requestCode == 0 ){
            if( resultCode == RESULT_OK ){
                if (in != null) {
                    //use to get scan result
                    String contents = in.getStringExtra("SCAN_RESULT");
                    String format = in.getStringExtra("SCAN_RESULT_FORMAT");
                    //Toast toast = Toast.makeText( this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG );
                    //toast.show();
                    if (ItemScanWS.WSEvent(contents) == true) {
                        M_ProductDBAdapter m_productDBAdapter = new M_ProductDBAdapter(ApplicationContext.getAppContext());
                        M_Product product = m_productDBAdapter.getM_ProductByUPC(contents);

                        C_OrderLine orderItem = new C_OrderLine();
                        orderItem.setM_Product_ID(product.getM_Product_ID());

                        if (product.getM_Locator_ID() == 0) {
                            orderItem.setM_Warehouse_ID(m_locatorDBAdapter.getM_Locator().getM_Warehouse_ID());
                            orderItem.setM_Locator_ID(m_locatorDBAdapter.getM_Locator().getM_Locator_ID());
                        } else {
                            orderItem.setM_Warehouse_ID(m_locatorDBAdapter.getM_Locator(product.getM_Locator_ID()).getM_Warehouse_ID());
                            orderItem.setM_Locator_ID(m_locatorDBAdapter.getM_Locator(product.getM_Locator_ID()).getM_Locator_ID());
                        }

                        orderItem.setC_Uom_ID(product.getC_Uom_ID());
                        orderItem.setProductName(product.getName());
                        orderItem.setQtyOrdered(1);
                        orderItem.setM_Pricelist_ID(product.getM_ProductList_ID());
                        orderItem.setPriceList(product.getPriceList());
                        orderItem.setPriceActual(product.getPriceStd());
                        orderItem.setPriceLimit(product.getPriceLimit());
                        orderItem.setLineNetAmt(product.getPriceStd());

                        c_orderLines.add(orderItem);

                        updateTotalView();

                        Fragment f = getFragmentManager().findFragmentById(R.id.article_list);
                        if (f instanceof ItemScanFragment) {
                            // do something with f
                            ((ItemScanFragment) f).refreshData();
                        }
                    }
                }
            }
        }
    }//onActivityResult

    private void updateOrder()
    {
        String UserId = SharedPrefUtil.getPersistedData(ApplicationContext.USERID, null);

        c_order.setC_BPartner_ID(c_bPartner.getC_BPartner_ID());
        c_order.setCustomerName(c_bPartner.getName());
        c_order.setC_BPartner_Location_ID(c_bPartner.getC_BPartner_Location_ID());
        c_order.setC_Bill_BPartner_ID(c_bPartner.getC_BPartner_ID());
        c_order.setC_Bill_Location_ID(c_bPartner.getC_BPartner_Location_ID());

        c_order.setDateOrdered(new Date());
        c_order.setIsCash(isCash);
        c_order.setSalesRep_ID(Long.parseLong(UserId));
        if (isCash)
        {
            c_order.setPaymentRule("B");
            c_order.setC_PaymentTerm_ID(Long.parseLong(PropUtil.getProperty("c_paymentterm_id_cash")));
            c_order.setC_DocTypeTarget_ID(Long.parseLong(PropUtil.getProperty("c_doctypetarget_id_cash")));
        }
        else {
            c_order.setPaymentRule("P");
            c_order.setC_PaymentTerm_ID(Long.parseLong(PropUtil.getProperty("c_paymentterm_id_credit")));
            c_order.setC_DocTypeTarget_ID(Long.parseLong(PropUtil.getProperty("c_doctypetarget_id_credit")));
        }

        ArrayList<C_OrderLine> c_orderItems = new ArrayList<>();

        BigDecimal subtotal = new BigDecimal(0);
        int lineNo = 0;
        Iterator<C_OrderLine> orderItemIterator = c_orderLines.iterator();
        while(orderItemIterator.hasNext()) {
            C_OrderLine nextOrderItem = orderItemIterator.next();
            C_OrderLine c_orderItem = new C_OrderLine();
            lineNo = lineNo + 10;
            c_orderItem.setLineNo(lineNo);
            if (nextOrderItem.getID() != 0)
                c_orderItem.setID(nextOrderItem.getID());
            if (nextOrderItem.getOrderID() != 0)
                c_orderItem.setOrderID(nextOrderItem.getOrderID());
            if (nextOrderItem.getC_Order_ID() != 0)
                c_orderItem.setC_Order_ID(nextOrderItem.getC_Order_ID());
            if (nextOrderItem.getC_OrderLine_ID() != 0)
                c_orderItem.setC_OrderLine_ID(nextOrderItem.getC_OrderLine_ID());
            c_orderItem.setC_BPartner_ID(c_bPartner.getC_BPartner_ID());
            c_orderItem.setC_BPartner_Location_ID(c_bPartner.getC_BPartner_Location_ID());
            c_orderItem.setDateOrdered(c_order.getDateOrdered());
            c_orderItem.setM_Product_ID(nextOrderItem.getM_Product_ID());
            c_orderItem.setProductName(nextOrderItem.getProductName());
            c_orderItem.setM_Warehouse_ID(nextOrderItem.getM_Warehouse_ID());
            c_orderItem.setM_Locator_ID(nextOrderItem.getM_Locator_ID());
            c_orderItem.setC_Uom_ID(nextOrderItem.getC_Uom_ID());
            c_orderItem.setQtyOrdered(nextOrderItem.getQtyOrdered());
            c_orderItem.setM_Pricelist_ID(nextOrderItem.getM_Pricelist_ID());
            c_orderItem.setPriceList(nextOrderItem.getPriceList());
            c_orderItem.setPriceActual(nextOrderItem.getPriceActual());
            c_orderItem.setPriceLimit(nextOrderItem.getPriceLimit());
            c_orderItem.setLineNetAmt(nextOrderItem.getLineNetAmt());
            c_orderItem.setDiscount((((nextOrderItem.getPriceList() - nextOrderItem.getPriceActual())/nextOrderItem.getPriceList())*100));
            c_orderItem.setC_Tax_ID(c_tax.getC_Tax_ID());

            subtotal = subtotal.add(BigDecimal.valueOf(nextOrderItem.getLineNetAmt()).movePointLeft(2));
            c_orderItems.add(c_orderItem);
        }

        BigDecimal multiplier = BigDecimal.valueOf(c_tax.getRate()).divide(new BigDecimal(10000), 12, BigDecimal.ROUND_HALF_UP);

        BigDecimal tax = subtotal.multiply(multiplier).setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal total = subtotal.add(tax);

        c_order.setM_Warehouse_ID(c_orderItems.get(0).getM_Warehouse_ID());
        c_order.setM_Pricelist_ID(c_orderItems.get(0).getM_Pricelist_ID());
        c_order.setTotalLines(subtotal.multiply(new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_UP)).intValueExact());
        c_order.setGrandTotal(total.multiply(new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_UP)).intValueExact());

        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra("c_order", c_order);
        intent.putExtra("c_orderItems", c_orderItems);
        intent.putExtra("isCash", isCash);
        intent.putExtra("mTax", c_tax);
        intent.putExtra("customer", c_bPartner);
        startActivity(intent);
    }

    private void resetData(){
        c_orderLines.clear();
        c_tax = new C_Tax();
        c_bPartner = null;

        TextView vSubtotal = (TextView) findViewById(R.id.subtotal);
        TextView vTax = (TextView) findViewById(R.id.tax);
        TextView vTotal = (TextView) findViewById(R.id.total);

        vSubtotal.setText("");
        vTax.setText("");
        vTotal.setText("");

        Fragment f = getFragmentManager().findFragmentById(R.id.article_list);
        if (f instanceof ItemScanFragment) {
            // do something with f
            ((ItemScanFragment) f).refreshData();
        }
    }

    private void updateTotalView()
    {
        BigDecimal subtotal = new BigDecimal(0);
        Iterator<C_OrderLine> orderItemIterator = c_orderLines.iterator();
        while(orderItemIterator.hasNext()) {
            C_OrderLine nextOrderItem = orderItemIterator.next();
            subtotal = subtotal.add(BigDecimal.valueOf(nextOrderItem.getLineNetAmt()).movePointLeft(2));
        }

        BigDecimal multiplier = BigDecimal.valueOf(c_tax.getRate()).divide(new BigDecimal(10000), 12, BigDecimal.ROUND_HALF_UP);

        BigDecimal tax = subtotal.multiply(multiplier).setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal total = subtotal.add(tax);

        TextView vSubtotal = (TextView) findViewById(R.id.subtotal);
        TextView vTax = (TextView) findViewById(R.id.tax);
        TextView vTotal = (TextView) findViewById(R.id.total);

        vSubtotal.setText(String.format( "%.2f", subtotal));
        vTax.setText(String.format( "%.2f", tax));
        vTotal.setText(String.format( "%.2f", total));
    }

    public ArrayList<C_OrderLine> getListOrderItems()
    {
        return this.c_orderLines;
    }

    public void setTax(C_Tax tax)
    {
        c_tax = tax;
        updateTotalView();
    }

    public void updateListOrderItems(ArrayList<C_OrderLine> newOrderItems)
    {
        this.c_orderLines = newOrderItems;

        updateTotalView();

        Fragment f = getFragmentManager().findFragmentById(R.id.article_list);
        if (f instanceof ItemScanFragment) {
            // do something with f
            ((ItemScanFragment) f).refreshData();
        }
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
        getMenuInflater().inflate(R.menu.order_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                openDrawer();
                return true;
            case R.id.action_checkout:
                if (c_orderLines.size() > 0) {
                    updateOrder();
                }
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
