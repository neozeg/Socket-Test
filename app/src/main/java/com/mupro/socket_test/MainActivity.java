package com.mupro.socket_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private UDPServer Server;
    private UdpHelper udphelper;

    private TextView mTvRec;
    private EditText mEtSend;
    private Button mBtnSend;
    private ListView mLvChat;

    private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
    private ChatMsgViewAdapter mChatMsgViewAdapter;

    private Thread tReceived;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //用于创建线程
        WifiManager manager = (WifiManager) this
                .getSystemService(Context.WIFI_SERVICE);
        udphelper = new UdpHelper(manager,getApplicationContext());
        //udphelper = new UdpHelper(manager);
        //传递WifiManager对象，以便在UDPHelper类里面使用MulticastLock
        //udphelper.addObserver(MsgReceiveService.this);
        //tReceived = new Thread(udphelper);
        //tReceived.start();

        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(udphelper);

        registerReceivers();
        setupViewComponents();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
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

    private void setupViewComponents(){

        mLvChat = (ListView) findViewById(R.id.listViewChat);
        mChatMsgViewAdapter = new ChatMsgViewAdapter(this,mDataArrays);
        mLvChat.setAdapter(mChatMsgViewAdapter);

        mTvRec = (TextView) findViewById(R.id.textViewMsgRec);
        mEtSend = (EditText) findViewById(R.id.editTextMsgSend);
        mBtnSend = (Button) findViewById(R.id.buttonSend);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**/
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        udphelper.send(mEtSend.getText().toString());
                        //udphelper.send(" ");
                    }
                }).start();
                ChatMsgEntity entity = new ChatMsgEntity();
                entity.setDate(Calendar.getInstance().getTime());
                entity.setMessageContent(mEtSend.getText().toString());
                entity.setMsgType(false);
                entity.setName("Me");
                mDataArrays.add(entity);
                mChatMsgViewAdapter.notifyDataSetChanged();


                //udphelper.send(mEtSend.getText().toString());
            }
        });
    }
    private void registerReceivers(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(UdpHelper.ACTION_UDP_MSG_RECEIVE);
        registerReceiver(mUDPReceiver,filter);
    }

    private void unregisterReceivers(){
        unregisterReceiver(mUDPReceiver);
    }

    private BroadcastReceiver mUDPReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(UdpHelper.ACTION_UDP_MSG_RECEIVE)){
                processUdpMsg(intent);
            }
        }
    };

    private void processUdpMsg(Intent intent){
        Log.i(TAG,intent.getAction());
        String str = new String(intent.getByteArrayExtra(UdpHelper.EXTRA_UDP_MSG_RECEIVE));
        str = str.trim();
        mTvRec.setText(str.toString());
        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setDate(Calendar.getInstance().getTime());
        entity.setMessageContent(str);
        entity.setMsgType(true);
        entity.setName("Server");
        mDataArrays.add(entity);
        mChatMsgViewAdapter.notifyDataSetChanged();
    }
}
