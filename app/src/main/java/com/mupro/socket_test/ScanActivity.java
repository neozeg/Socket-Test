package com.mupro.socket_test;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ScanActivity extends Activity {
    private final static String TAG = "ScanActivity";


    private ListView mLvHosts;
    private Button mBtnScan;
    private List<HostObject> mHostList ;
    private HostListAdapter mHostListAdapter;
    private NetTool netTool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        setResult(Activity.RESULT_CANCELED);

        netTool = new NetTool(getApplicationContext());
        setupViewComponents();
        netTool.scanHost();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceivers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceivers();
    }

    private void setupViewComponents(){
        mLvHosts = (ListView) findViewById(R.id.listViewHosts);
        mHostList = new ArrayList<HostObject>();
        mHostListAdapter = new HostListAdapter(getApplicationContext(),mHostList);
        mLvHosts.setAdapter(mHostListAdapter);
        mHostListAdapter.notifyDataSetChanged();
        mLvHosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HostObject host = mHostList.get(position);
                Intent intent = new Intent();
                intent.putExtra(NetTool.EXTRA_HOST_IP,host.getHostAddress());
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });

        //mBtnScan = (Button) findViewById(R.id.buttonScan);

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(NetTool.ACTION_FOUND_HOST)){
                //Log.i(TAG,NetTool.ACTION_FOUND_HOST);
                HostObject host = new HostObject();
                host.setHostName(intent.getStringExtra(NetTool.EXTRA_HOST_IP));
                host.setHostAddress(intent.getStringExtra(NetTool.EXTRA_HOST_IP));
                mHostList.add(host);
                mHostListAdapter.notifyDataSetChanged();
            }
        }
    };

    private void registerReceivers(){
        IntentFilter filter  = new IntentFilter();
        filter.addAction(NetTool.ACTION_FOUND_HOST);
        registerReceiver(mReceiver,filter);
    }
    private void unregisterReceivers(){
        unregisterReceiver(mReceiver);
    }
}
