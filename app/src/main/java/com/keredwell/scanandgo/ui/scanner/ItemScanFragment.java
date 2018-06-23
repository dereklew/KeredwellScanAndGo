package com.keredwell.scanandgo.ui.scanner;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.keredwell.scanandgo.R;
import com.keredwell.scanandgo.data.C_OrderLine;

import java.math.BigDecimal;
import java.util.ArrayList;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class ItemScanFragment extends ListFragment {
    private static final String TAG = makeLogTag(ItemScanFragment.class);

    private ItemScanFragment.Callback callback = orderCallback;

    private ArrayList<C_OrderLine> mItem = new ArrayList<>();
    /**
     * A callback interface. Called whenever a item has been selected.
     */
    public interface Callback {
        void onItemSelected(int id);
    }

    /**
     * A dummy no-op implementation of the Callback interface. Only used when no active Activity is present.
     */
    private static final ItemScanFragment.Callback orderCallback = new ItemScanFragment.Callback() {
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
        mItem = ((ItemScanActivity)getActivity()).getListOrderItems();

        setListAdapter(new ItemScanFragment.MyListAdapter());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
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
        if (!(context instanceof ItemScanFragment.Callback)) {
            throw new IllegalStateException("Activity must implement callback interface.");
        }

        callback = (ItemScanFragment.Callback) context;
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_order, container, false);
            }

            final C_OrderLine item = (C_OrderLine) getItem(position);
            ((TextView) convertView.findViewById(R.id.productname)).setText(item.getProductName());
            ((TextView) convertView.findViewById(R.id.quantity)).setText(String.valueOf(item.getQtyOrdered()));
            ((TextView) convertView.findViewById(R.id.unitprice)).setText(String.format( "%.2f", BigDecimal.valueOf(item.getPriceActual()).movePointLeft(2)));
            ((TextView) convertView.findViewById(R.id.price)).setText(String.format( "%.2f", BigDecimal.valueOf(item.getLineNetAmt()).movePointLeft(2)));

            Button btnPlus = (Button) convertView.findViewById(R.id.plus);
            btnPlus.setTag(new Integer(position+50));
            btnPlus.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int position = (int)view.getTag()-50;
                    C_OrderLine item = (C_OrderLine) getItem(position);
                    item.setQtyOrdered(item.getQtyOrdered() + 1);
                    item.setLineNetAmt(item.getPriceActual() * item.getQtyOrdered());
                    mItem.set(position, item);
                    ((ItemScanActivity)getActivity()).updateListOrderItems(mItem);
                }
            });

            Button btnMinus = (Button) convertView.findViewById(R.id.minus);
            btnMinus.setTag(new Integer(position+100));
            btnMinus.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int position = (int)view.getTag()-100;
                    C_OrderLine item = (C_OrderLine) getItem(position);
                    if (item.getQtyOrdered() == 1)
                    {
                        mItem.remove(position);
                    }
                    else
                    {
                        item.setQtyOrdered(item.getQtyOrdered() - 1);
                        item.setLineNetAmt(item.getPriceActual() * item.getQtyOrdered());
                        mItem.set(position, item);
                    }
                    ((ItemScanActivity)getActivity()).updateListOrderItems(mItem);
                }
            });

            return convertView;
        }
    }

    public ItemScanFragment() {
    }
}
