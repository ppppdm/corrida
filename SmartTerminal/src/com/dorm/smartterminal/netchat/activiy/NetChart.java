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
import com.dorm.smartterminal.netchat.component.VideoPlayer;
import com.dorm.smartterminal.netchat.component.VideoRecorder;
import com.dorm.smartterminal.service.NetCommunicationService;

public class NetChart extends Activity {

    
    Messenger mMessenger = new Messenger(new IncomingHandler());
    
    class IncomingHandler extends Handler{

		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
    	
    }
    /*
     * ui
     */
    private Button button_accept;
    private Button button_refuse;
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
     * net
     */
    private Messenger mService;

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
            
            try {
                Message msg = Message.obtain(null,
                        NetCommunicationService.MSG_LOCAL_QUERY);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }
            

            // As part of the sample, tell the user what happened.
            // Toast.makeText(getApplicationContext(),
            // R.string.remote_service_connected, Toast.LENGTH_SHORT)
            // .show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            // mCallbackText.setText("Disconnected.");

            // As part of the sample, tell the user what happened.
            // Toast.makeText(getApplicationContext(),
            // R.string.remote_service_disconnected, Toast.LENGTH_SHORT)
            // .show();
        }
    };

    /*
     * logic
     */

    OnClickListener onclick = new OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
            case R.id.button_accept:
                sendMessageToService(NetCommunicationService.MSG_LOCAL_QUERY);
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
        doBindService();

        /*
         * video recorder
         */
        initVideoRecorder();
        startPreview();

        /*
         * video player
         */
        initVideoPlayer();
    }

    /*
     * ui
     */
    private void initButtons() {

        button_accept = (Button) findViewById(R.id.button_accept);
        button_refuse = (Button) findViewById(R.id.button_refuse);

        // Set button listener
        button_accept.setOnClickListener(onclick);
        button_refuse.setOnClickListener(onclick);

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
     * video player
     */

    private void initVideoPlayer() {

        videoPlayer = new VideoPlayer(this);

    }

    private void showImage(byte[] image) {

        videoPlayer.showImage(image);
    }

    /*
     * net
     */
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
            mService.send(msg);
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "send msg", Toast.LENGTH_SHORT).show();
    }

    public void sendImageToService(byte[] image) {

        // TODO add net code here
        // LogUtil.log(this, "" + image.length);
        showImage(image);
    }
}
