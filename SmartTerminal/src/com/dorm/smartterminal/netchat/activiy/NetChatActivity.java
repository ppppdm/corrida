package com.dorm.smartterminal.netchat.activiy;

import com.dorm.smartterminal.R;
import com.dorm.smartterminal.service.NetCommunicationService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class NetChatActivity extends Activity {
    
    Button button_call;
    Button button_refuse;
    Messenger mService;
    Intent mServiceIntent = new Intent(this, NetCommunicationService.class);
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service. We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mService = new Messenger(service);
            // mCallbackText.setText("Attached.");

            // We want to monitor the service for as long as we are
            // connected to it.
            /*
            try {
                Message msg = Message.obtain(null,
                        NetCommunicationService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);

                // Give it some value as an example.
                msg = Message.obtain(null, NetCommunicationService.MSG_SET_VALUE,
                        this.hashCode(), 0);
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }
            */

            // As part of the sample, tell the user what happened.
            //Toast.makeText(getApplicationContext(),
            //        R.string.remote_service_connected, Toast.LENGTH_SHORT)
            //       .show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            // mCallbackText.setText("Disconnected.");

            // As part of the sample, tell the user what happened.
            //Toast.makeText(getApplicationContext(),
            //        R.string.remote_service_disconnected, Toast.LENGTH_SHORT)
            //        .show();
        }
    };
    
    OnClickListener onclick = new OnClickListener(){

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch(v.getId()){
            case R.id.button_call:
                sendMessageToService(NetCommunicationService.MSG_LOCAL_QUERY);
                break;
            case R.id.button_refuse:
                sendMessageToService(NetCommunicationService.MSG_LOCAL_REFUSE);
                break;
            default:
                break;
            }
        }
        
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.st__netchat_test);
        
        button_call = (Button)findViewById(R.id.button_call);
        button_refuse = (Button)findViewById(R.id.button_refuse);
        
        
        //Bind NetCommunicatoin Service
        doBindService();
        
        //Set button listener
        button_call.setOnClickListener(null);
        button_refuse.setOnClickListener(null);
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        doUnBindService();
        super.onDestroy();
    }
    
    private void doBindService(){
        bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }
    
    private void doUnBindService(){
        unbindService(mConnection);
    }

    private void sendMessageToService(int Define_msg){
        Message msg = Message.obtain(null,Define_msg);
        try {
            mService.send(msg);
        }
        catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "send msg", Toast.LENGTH_SHORT).show();
    }
    
}
