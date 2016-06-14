package com.mupro.socket_test;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private UDPServer Server;
    private UdpHelper udphelper;

    private TextView mTvRec;
    private EditText mEtSend;
    private Button mBtnSend;
    private Thread tReceived;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //用于创建线程
        WifiManager manager = (WifiManager) this
                .getSystemService(Context.WIFI_SERVICE);
        udphelper = new UdpHelper(manager);

        //传递WifiManager对象，以便在UDPHelper类里面使用MulticastLock
        //udphelper.addObserver(MsgReceiveService.this);
        tReceived = new Thread(udphelper);
        tReceived.start();

        mTvRec = (TextView) findViewById(R.id.textViewMsgRec);
        mEtSend = (EditText) findViewById(R.id.editTextMsgSend);
        mBtnSend = (Button) findViewById(R.id.buttonSend);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                udphelper.send("test");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
