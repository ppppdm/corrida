package com.dorm.smartterminal.netchat.activiy;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dorm.smartterminal.R;
import com.dorm.smartterminal.global.util.ActivityUtil;

import com.dorm.smartterminal.netchat.component.AudioPlayer;
import com.dorm.smartterminal.netchat.component.AudioRecorder;

import com.dorm.smartterminal.global.util.LogUtil;

import com.dorm.smartterminal.netchat.component.VideoPlayer;
import com.dorm.smartterminal.netchat.component.VideoRecorder;
import com.dorm.smartterminal.service.NetCommunicationService;

public class NetChart extends Activity {

    Messenger mMessenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            
            switch(msg.what){
            case NetCommunicationService.MSG_REMOTE_REFUSE:
            	LogUtil.log(this, "net chat got MSG_REMOTE_REFUSE");
            	break;
            case NetCommunicationService.MSG_HOST_ERROR:
            	LogUtil.log(this, "net chat got MSG_HOST_ERROR");
            	break;
            }

        }
    }

    /*
     * ui
     */
    private Button button_accept;
    private Button button_refuse;
    private Button button_call;
    public SurfaceView surfaceView = null;
    public ImageView imageView = null;

    /*
     * video recorder
     */
    VideoRecorder videoRecorder = null;

    /*
     * video player
     */
    VideoPlayer videoPlayer = null;
    
    /*
     * AudioPlayer
     */
    AudioPlayer audioPlayer = null;
    
    /*
     * AudioRecorder
     */
    AudioRecorder audioRecorder = null;

    /*
     * net
     */
    private Messenger mService;

    private String ip = "";

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
          
            mService = new Messenger(service);
          
            try {
                Message msg = Message.obtain(null, NetCommunicationService.MSG_REGISTE);
                msg.replyTo = mMessenger;
                mService.send(msg);
                LogUtil.log(this, "send MSG_REGISTE");
            }
            catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }
        }

        public void onServiceDisconnected(ComponentName className) {
          
            mService = null;
           
        }
    };

    /*
     * logic
     */

    OnClickListener onclick = new OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
            case R.id.button_call:
            	LogUtil.log(this, "send msg MSG_LOCAL_QUERY");
            	sendMessageToService(NetCommunicationService.MSG_LOCAL_QUERY);
                startRecorder();
                break;
            case R.id.button_accept:
                sendMessageToService(NetCommunicationService.MSG_LOCAL_OK);
                startRecorder();
                break;

            case R.id.button_refuse:
                sendMessageToService(NetCommunicationService.MSG_LOCAL_REFUSE);
                break;

            case R.id.back:
                ActivityUtil.closeActivity(NetChart.this);

            default:
                break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.st__netchat);

        /*
         * ui
         */
        initButtons();
        initSurfaceView();
        initImageView();

        /*
         * net
         */
        getIPFromIntent();
        doBindService();

        /*
         * video recorder
         */
        initVideoRecorder();
        ///startPreview();

        /*
         * video player
         */
        //initVideoPlayer();
        
        /*
         * audio player
         */
        //initAudioPlayer();
        
        /*
         * audio recorder
         */
        //initAudioRecoder();
    }

    /*
     * ui
     */
    private void initButtons() {

        button_accept = (Button) findViewById(R.id.button_accept);
        button_refuse = (Button) findViewById(R.id.button_refuse);
        button_call = (Button) findViewById(R.id.button_call);

        // Set button listener
        button_accept.setOnClickListener(onclick);
        button_refuse.setOnClickListener(onclick);
        button_call.setOnClickListener(onclick);

        findViewById(R.id.back).setOnClickListener(onclick);
    }

    private void initSurfaceView() {

        surfaceView = (SurfaceView) findViewById(R.id.surface_view);

    }

    private void initImageView() {

        imageView = (ImageView) findViewById(R.id.image_view);
    }

    /*
     * video recorder
     */

    private void initVideoRecorder() {

        videoRecorder = new VideoRecorder(this);
    }

    private void startPreview() {

        if (null != videoRecorder) {

            videoRecorder.startPreview();
        }

    }

    private void startRecorder() {

        if (null != videoRecorder) {

            videoRecorder.startRecorder();

        }

    }

    private void stopRecorder() {

        if (null != videoRecorder) {

            videoRecorder.stopRecorder();

        }

    }

   

    /*
     * net
     */
    private void getIPFromIntent() {

        Intent intent = getIntent();
        ip = intent.getStringExtra("ip");

        LogUtil.log(this, "get ip from intent : [ " + ip + " ]");

    }

    private void doBindService() {

        Intent mServiceIntent = new Intent(this, NetCommunicationService.class);
        bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {

        stopRecorder();
        
        
        doUnBindService();
        super.onDestroy();
    }

    private void doUnBindService() {

        unbindService(mConnection);
    }

    private void sendMessageToService(int Define_msg) {

        Message msg = Message.obtain(null, Define_msg);
        try {
            // IP
            
            Bundle data = new Bundle();
            data.putString("IP", ip);
            msg.setData(data);
            
            mService.send(msg);
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "send msg", Toast.LENGTH_SHORT).show();
    }

    
}
