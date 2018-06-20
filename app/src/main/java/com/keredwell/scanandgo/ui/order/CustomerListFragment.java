package com.keredwell.scanandgo.ui.order;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.keredwell.scanandgo.R;
import com.keredwell.scanandgo.data.C_BPartner;

import java.util.ArrayList;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

/**
 * Shows a list of all available quotes.
 * <p/>
 * Created by Andreas Schrade on 14.12.2015.
 */
public class CustomerListFragment extends ListFragment {
    private static final String TAG = makeLogTag(CustomerListFragment.class);

    private Callback callback = customerCallback;

    private ArrayList<C_BPartner> mItem = new ArrayList<>();
    /**
     * A callback interface. Called whenever a item has been selected.
     */
    public interface Callback {
        void onItemSelected(int id);
    }

    /**
     * A dummy no-op implementation of the Callback interface. Only used when no active Activity is present.
     */
    private static final Callback customerCallback = new Callback() {
        @Override
        public void onItemSelected(int id) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        refreshData();

        setHasOptionsMenu(true);
    }

    public void refreshData(){
        mItem = ((CustomerListActivity)getActivity()).getListCustomers();

        setListAdapter(new MyListAdapter());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // notify callback about the selected list item
        callback.onItemSelected(position);
    }

    /**
     * onAttach(Context) is not called on pre API 23 versions of Android.
     * onAttach(Activity) is deprecated but still necessary on older devices.
     */
    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }

    /**
     * Deprecated on API 23 but still necessary for pre API 23 devices.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    /**
     * Called when the fragment attaches to the context
     */
    protected void onAttachToContext(Context context) {
        if (!(context instanceof Callback)) {
            throw new IllegalStateException("Activity must implement callback interface.");
        }

        callback = (Callback) context;
    }

    private class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mItem.size();
        }

        @Override
        public Object getItem(int position) {
            return mItem.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_order_customer, container, false);
            }

            if (getCount()>0) {
                final C_BPartner item = (C_BPartner) getItem(position);
                ((TextView) convertView.findViewById(R.id.name)).setText(item.getName());
                ((TextView) convertView.findViewById(R.id.address)).setText(item.getAddress());
            }
            return convertView;
        }
    }

    public CustomerListFragment() {
    }
}