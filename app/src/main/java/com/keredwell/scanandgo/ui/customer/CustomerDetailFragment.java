package com.keredwell.scanandgo.ui.customer;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.Bind;

import com.keredwell.scanandgo.ApplicationContext;
import com.keredwell.scanandgo.data.C_BPartner;
import com.keredwell.scanandgo.ui.base.BaseActivity;
import com.keredwell.scanandgo.R;
import com.keredwell.scanandgo.ui.base.BaseFragment;
import com.keredwell.scanandgo.util.LogUtil;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class CustomerDetailFragment extends BaseFragment {
    private static final String TAG = makeLogTag(CustomerDetailFragment.class);
    /**
     * The argument represents the data of this fragment.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_NAME = "item_name";
    public static final String ARG_ITEM_ADDRESS = "item_address";
    public static final String ARG_ITEM_POSTAL = "item_postal";
    public static final String ARG_ITEM_TEL = "item_tel";

    private C_BPartner mItem;

    @Bind(R.id.customername)
    TextView customername;

    @Bind(R.id.address)
    TextView address;

    @Bind(R.id.postal)
    TextView postal;

    @Bind(R.id.tel)
    TextView tel;

    @Bind(R.id.backdrop)
    ImageView backdropImg;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);

            Bundle arguments = getArguments();

            mItem = new C_BPartner();

            if (arguments != null) {
                mItem.setC_BPartner_ID(arguments.getInt(ARG_ITEM_ID));
                mItem.setName(arguments.getString(ARG_ITEM_NAME));
                mItem.setAddress(arguments.getString(ARG_ITEM_ADDRESS));
                mItem.setPostal(arguments.getString(ARG_ITEM_POSTAL));
                mItem.setPhone(arguments.getString(ARG_ITEM_TEL));
            }

            setHasOptionsMenu(true);
        } catch (Exception e) {
            LogUtil.logE(TAG, e.getMessage(), e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflateAndBind(inflater, container, R.layout.fragment_customer_detail);

        if (!((BaseActivity) getActivity()).providesActivityToolbar()) {
            ((BaseActivity) getActivity()).setToolbar((Toolbar) rootView.findViewById(R.id.toolbar));
        }

        if (mItem != null) {
            loadBackdrop();
            collapsingToolbar.setTitle(ApplicationContext.getAppContext().getString(R.string.title_customers_detail));
            customername.setText(mItem.getName());
            address.setText(mItem.getAddress());
            postal.setText(mItem.getPostal());
            tel.setText(mItem.getPhone());
        }

        return rootView;
    }

    private void loadBackdrop() {
        Glide.with(this).load(R.drawable.background).centerCrop().into(backdropImg);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static CustomerDetailFragment newInstance(Bundle arguments) {
        CustomerDetailFragment fragment = new CustomerDetailFragment();

        fragment.setArguments(arguments);
        return fragment;
    }

    public CustomerDetailFragment() {}
}