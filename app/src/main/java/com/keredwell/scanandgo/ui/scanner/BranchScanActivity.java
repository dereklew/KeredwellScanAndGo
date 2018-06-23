package com.keredwell.scanandgo.ui.scanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.keredwell.scanandgo.ApplicationContext;
import com.keredwell.scanandgo.data.AD_Org;
import com.keredwell.scanandgo.data.M_Pricelist;
import com.keredwell.scanandgo.data.M_Pricelist_Version;
import com.keredwell.scanandgo.dbhelper.AD_OrgDBAdapter;
import com.keredwell.scanandgo.dbhelper.M_PricelistDBAdapter;
import com.keredwell.scanandgo.dbhelper.M_Pricelist_VersionDBAdapter;
import com.keredwell.scanandgo.ui.base.BaseActivity;
import com.keredwell.scanandgo.R;
import com.keredwell.scanandgo.util.SharedPrefUtil;
import com.keredwell.scanandgo.webservice.AD_OrgWS;
import com.keredwell.scanandgo.webservice.BranchScanWS;
import com.keredwell.scanandgo.webservice.M_PricelistWS;
import com.keredwell.scanandgo.webservice.M_Pricelist_VersionWS;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class BranchScanActivity extends BaseActivity {
    private static final String TAG = makeLogTag(BranchScanActivity.class);

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_branchscan);

        setupToolbar();

        FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                Intent intent = new Intent(BranchScanActivity.this, SimpleScannerActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent in ) {
        // TODO Auto-generated method stub
        if( requestCode == 0 ){
            if( resultCode == RESULT_OK ){
                //use to get scan result
                String contents = in.getStringExtra( "SCAN_RESULT" );
                String format =  in.getStringExtra( "SCAN_RESULT_FORMAT");
                //Toast toast = Toast.makeText( this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG );
                //toast.show();

                if (BranchScanWS.WSEvent(contents) == true) {
                    Intent intent = new Intent(this, ItemScanActivity.class);
                    startActivity(intent);
                }

                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle(R.string.title_branch_not_found);
                alertDialog.setMessage(getApplicationContext().getString(R.string.text_branch_not_found));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getApplicationContext().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(BranchScanActivity.this, BranchScanActivity.class);
                            startActivity(intent);
                        }
                    });
                alertDialog.show();
            }
        }
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }
}
