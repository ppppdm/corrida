package com.dorm.smartterminal.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import com.dorm.smartterminal.R;
import com.dorm.smartterminal.global.util.LogUtil;
import com.dorm.smartterminal.global.util.WorkLocker;
import com.dorm.smartterminal.netchat.activiy.NetChart;
import com.dorm.smartterminal.netchat.component.AudioPlayer;
import com.dorm.smartterminal.netchat.component.AudioRecorder;
import com.dorm.smartterminal.netchat.component.VideoPlayer;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class NetCommunicationService extends Service {

    private netCommandHandler serviceHandler = new netCommandHandler();
    private Messenger messenger;
    private final WorkLocker netComnLock = new WorkLocker();

    private ServerSocket cmdServerSocket = null;
    private Socket cmdSocket = null;
    
    private ServerSocket audioDataServerSocket = null;
    private Socket audioDataSocket = null;
    
    
    private ServerSocket videoDataServerSocket = null;
    private Socket videoDataSocket = null;

    private Messenger activityMessager = null;

    private Thread serviceThread = null;
    
    private VideoPlayer videoPlayer = null;

    private Vector<Socket> clientList = new Vector<Socket>();

    static final int NET_COMMUNICATE_SERVER_TIMEOUT = 30;
    static final int NET_COMMUNICATE_SERVER_PORT = 6000;
    static final int NET_COMMUNICATE_SERVER_BACKLOG = 5;
    String TARGET_IP = null;

    static final int NET_COMMUNICATE_AUDIO_PORT = 6001;
    
    public static final int NET_COMMUNICATE_VIDEO_PORT = 6002;
    
    
    

    public static final int MSG_START_SERVICE = 1;
    public static final int MSG_REMOTE_CONNECTED = 2;
    public static final int MSG_REMOTE_QUERY = 3;
    public static final int MSG_REMOTE_REFUSE = 4;
    public static final int MSG_LOCAL_QUERY = 5;
    public static final int MSG_LOCAL_REFUSE = 6;
    public static final int MSG_FINISH_SERVICE = 7;
    public static final int MSG_REGISTE = 8;
    public static final int MSG_HOST_ERROR = 9;
    public static final int MSG_HOST_BUSSY = 10;
    public static final int MSG_WAIT_FOR_REMOTE = 11;
    public static final int MSG_REMOTE_OK = 12;
    public static final int MSG_START_DATA_CONNECT = 13;
    public static final int MSG_LOCAL_OK = 14;
    public static final int MSG_SHOW_REMOTE_IMG = 15;
    public static final int MSG_SHOW_LOCAL_IMG = 16;
    
    class netCommandHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // super.handleMessage(msg);
            switch (msg.what) {
            case MSG_START_SERVICE:
            	LogUtil.log(this,"MSG_START_SERVICE");
                startCmdService();
                break;
            case MSG_REMOTE_CONNECTED:
            case MSG_REMOTE_QUERY:
            	LogUtil.log(this,"MSG_REMOTE_QUERY");
                startRemoteTask(msg.arg1);
                break;
            case MSG_REMOTE_REFUSE:
            	LogUtil.log(this,"MSG_REMOTE_REFUSE");
                finishRemoteTask();
                break;
            case MSG_FINISH_SERVICE:
            	LogUtil.log(this,"MSG_FINISH_SERVICE");
                finishService();
                break;
            case MSG_LOCAL_QUERY:
            	LogUtil.log(this,"MSG_LOCAL_QUERY");
                startLocalTask(msg);
                break;
            case MSG_LOCAL_REFUSE:
            	LogUtil.log(this,"MSG_LOCAL_REFUSE");
                finishLocalTask();
                break;
            case MSG_REGISTE:
            	LogUtil.log(this,"MSG_REGISTE");
                doRegiste(msg);
                break;
            case MSG_WAIT_FOR_REMOTE:
            	LogUtil.log(this,"MSG_WAIT_FOR_REMOTE");
                waitForRemote();
                break;
            case MSG_START_DATA_CONNECT:
            	LogUtil.log(this,"MSG_START_DATA_CONNECT");
                startDataConnect();
                break;
            case MSG_LOCAL_OK:
            	LogUtil.log(this,"MSG_LOCAL_OK");
                startDataServer();
                break;
            default:
                super.handleMessage(msg);
            }
        }

    }

    // start the CmdService at port NET_COMMUNICATE_SERVER_PORT
    private void startCmdService() {

        // create new thread for server to accept
        serviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Before get next task set no task
                    netComnLock.setNoTask();
                    
                    // globla init
                    activityMessager = null;

                    cmdServerSocket = new ServerSocket(NET_COMMUNICATE_SERVER_PORT);
                    

                    Socket socket = cmdServerSocket.accept();
                    
                    

                    // remote had already connected to server
                    if (netComnLock.setHasTask()) {
                        
                        cmdSocket = socket;
                        
                        
                        
                        // stop CmdServer
                        stopCmdServer();
                        
                     // if had no task before ,begin this task
                        Message msg = Message.obtain(null, NetCommunicationService.MSG_REMOTE_QUERY);
                        serviceHandler.sendMessage(msg);
                    }

                    else {
                        // already had task
                        // close socket
                        socket.close();
                        // stop CmdService
                        stopCmdServer();
                    }
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });

        serviceThread.start();
        
        // may be a new Thread for video service
        
        new Thread(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                //video server socket
                try {
                    videoDataServerSocket = new ServerSocket(NET_COMMUNICATE_VIDEO_PORT);
                    // aceept video connect
                    Socket socket_v = videoDataServerSocket.accept();
                    
                    // video ,maybe should after cmdServerSocket accept
                    videoDataSocket = socket_v;
                    
                    
                    // init videoPlayer
                    videoPlayer = new VideoPlayer();
                    
                    videoPlayer.initVideoPlayer(videoDataSocket);
                    
                    // set messenger
                    if (activityMessager!=null){
                        videoPlayer.setMessenger(activityMessager);
                    }
                    
                    // startvideoPlayer
                    videoPlayer.startVideoPlayer();
                    
                    //close videoDataServerSocket
                    videoDataServerSocket.close();
                    
                    
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                
                
            }}).start();
        
    }

    private void stopCmdServer() {
        // close the cmdServerSocket

        if (cmdServerSocket != null) {
            if (!cmdServerSocket.isClosed()) {
                try {
                    cmdServerSocket.close();
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            cmdServerSocket = null;

        }
    }

    // finish the service
    private void finishService() {
        // create new thread to do finishService
        new Thread(new Runnable() {

            @Override
            public void run() {
                // close all client socket and finish task thread
                int clientNum = clientList.size();
                for (int i = 0; i < clientNum; i++) {

                    // send refuse to all clients

                    // close clients
                    try {
                        clientList.get(i).close();
                    }
                    catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

                // close server socket and finish service thread
                try {
                    cmdServerSocket.close();
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (serviceThread.isAlive()) {
                    try {
                        serviceThread.join();
                    }
                    catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    // remote task for get remote cmd
    private void startRemoteTask(int series) {
        
        //get remote IP
        String remoteIP = cmdSocket.getInetAddress().getHostAddress();
        
        // not need send msg to activity, need send remote IP to activity
        Intent intent = new Intent(this, NetChart.class);
        intent.putExtra("ip", remoteIP);
        
        // video socket
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // start activity
        this.startActivity(intent);
        
        // void to do
        // send msg remote call to activity
        
        
    }

    
    private void startLocalTask(Message msg) {

        if (netComnLock.setHasTask()) {
            // if had no task before ,begin this task

            // get remote IP address
            TARGET_IP = msg.getData().getString("IP");

            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        // stop cmd server
                        stopCmdServer();

                        // connect to remote
                        Socket socket = new Socket(TARGET_IP, NET_COMMUNICATE_SERVER_PORT);
                        cmdSocket = socket;
                        
                        LogUtil.log(this, "connect to server " + cmdSocket.isConnected());
                        LogUtil.log(this, "" +cmdSocket);

                        Message msg = Message.obtain(null, NetCommunicationService.MSG_WAIT_FOR_REMOTE);
                        serviceHandler.sendMessage(msg);

                    }

                    catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                        // connect to an nonexisted host, send message to
                        // activity
                        Message msg2 = Message.obtain(null, NetCommunicationService.MSG_HOST_ERROR);

                        try {
                            activityMessager.send(msg2);
                        }
                        catch (RemoteException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        // send msg to server to startCmdServer
                        Message msg = Message.obtain(null, NetCommunicationService.MSG_START_SERVICE);
                        serviceHandler.sendMessage(msg);
                    }
                    catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                        // connect to remote error, host is bussy, send to
                        // activity
                        Message msg2 = Message.obtain(null, NetCommunicationService.MSG_HOST_BUSSY);

                        try {
                            activityMessager.send(msg2);
                        }
                        catch (RemoteException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        // send msg to server to startCmdServer
                        Message msg = Message.obtain(null, NetCommunicationService.MSG_START_SERVICE);
                        serviceHandler.sendMessage(msg);
                    }

                }
            }).start();
        }
        else {
            // already has task

            // stop cmd server
            stopCmdServer();

        }

    }

    private void waitForRemote() {
        // start new thread to wait for remote
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                try {
                    DataInputStream in = new DataInputStream(cmdSocket.getInputStream());
                    int re = in.readInt();
                    if (re == MSG_REMOTE_OK) {
                        // remote ok, ready to connect to remote data server

                        Message msg = Message.obtain(null, NetCommunicationService.MSG_START_DATA_CONNECT);
                        serviceHandler.sendMessage(msg);
                    }
                    else if (re == MSG_REMOTE_REFUSE) {
                        // remote refuse, send to activity
                        Message msg2 = Message.obtain(null, NetCommunicationService.MSG_REMOTE_REFUSE);

                        try {
                            activityMessager.send(msg2);
                        }
                        catch (RemoteException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        // send msg to server to startCmdServer
                        Message msg = Message.obtain(null, NetCommunicationService.MSG_START_SERVICE);
                        serviceHandler.sendMessage(msg);
                    }
                    else {
                        // get error cmd, as remote refuse
                        // remote refuse, send to activity
                        Message msg2 = Message.obtain(null, NetCommunicationService.MSG_REMOTE_REFUSE);

                        try {
                            activityMessager.send(msg2);
                        }
                        catch (RemoteException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        // send msg to server to startCmdServer
                        Message msg = Message.obtain(null, NetCommunicationService.MSG_START_SERVICE);
                        serviceHandler.sendMessage(msg);
                    }

                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    // remote refuse, send to activity
                    Message msg2 = Message.obtain(null, NetCommunicationService.MSG_REMOTE_REFUSE);

                    try {
                        activityMessager.send(msg2);
                    }
                    catch (RemoteException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    // send msg to server to startCmdServer
                    Message msg = Message.obtain(null, NetCommunicationService.MSG_START_SERVICE);
                    serviceHandler.sendMessage(msg);
                }
                finally {
                    try {
                        cmdSocket.close();
                    }
                    catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        LogUtil.log(this, "close cmdSocket IOException");
                    }
                    cmdSocket = null;
                }
            }
        }).start();
    }

    private void startDataConnect() {
        // start a thread to connect remote data server
        
        new Thread(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    audioDataSocket = new Socket(TARGET_IP,NET_COMMUNICATE_AUDIO_PORT);
                    
                    // init audio service 
                    AudioRecorder audioRecorder = new AudioRecorder();
                    int len = audioRecorder.initAudioRecorder(audioDataSocket);
                    //Toast.makeText(getApplicationContext(), "recorder " + len, Toast.LENGTH_SHORT).show();
                    LogUtil.log(this,"recorder " + len);
                    
                    AudioPlayer audioPlayer = new AudioPlayer();
                    audioPlayer.initAudioPlayer(audioDataSocket);
                    
                    LogUtil.log(this,"start audio service after connnect");
                    // start audio service
                    audioRecorder.start();
                    audioPlayer.start();
                    
                    
                    
                }
                catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    
                    // void to do
                    LogUtil.log(this, "data connect UnknownHostException");
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    
                    // void to do
                    LogUtil.log(this, "data connect IOException");
                    
                    Message msg2 = Message.obtain(null, NetCommunicationService.MSG_REMOTE_REFUSE);

                    try {
                        activityMessager.send(msg2);
                    }
                    catch (RemoteException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    // send msg to server to startCmdServer
                    Message msg = Message.obtain(null, NetCommunicationService.MSG_START_SERVICE);
                    serviceHandler.sendMessage(msg);
                    
                    try {
                        audioDataSocket.close();
                    }
                    catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    
                    // send msg 
                }
                
                
            }}).start();

        //
    }
    
    private void startDataServer(){
        // start a thread to run audio data server
        
        new Thread(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    audioDataServerSocket = new ServerSocket(NET_COMMUNICATE_AUDIO_PORT);
                    
                    audioDataSocket = audioDataServerSocket.accept();
                    
                    
                    // init audio service
                    
                    AudioRecorder audioRecorder = new AudioRecorder();
                    int len = audioRecorder.initAudioRecorder(audioDataSocket);
                    //Toast.makeText(getApplicationContext(), "recorder " + len, Toast.LENGTH_SHORT).show();
                    AudioPlayer audioPlayer = new AudioPlayer();
                    audioPlayer.initAudioPlayer(audioDataSocket);
                    
                    LogUtil.log(this,"start audio service");
                    // start audio service
                    audioRecorder.start();
                    audioPlayer.start();
                    
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    
                    
                    LogUtil.log(this, "data server IOException");
                    
                    //send msg to activity
                    Message msg2 = Message.obtain(null, NetCommunicationService.MSG_REMOTE_REFUSE);

                    try {
                        activityMessager.send(msg2);
                    }
                    catch (RemoteException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    // send msg to server to startCmdServer
                    Message msg = Message.obtain(null, NetCommunicationService.MSG_START_SERVICE);
                    serviceHandler.sendMessage(msg);
                    
                    
                    try {
                        audioDataSocket.close();
                    }
                    catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                
                
                
            }}).start();
        
        // start a thread to send ok to remote
        new Thread(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                //cmdSocket.
                try {
                    DataOutputStream out = new DataOutputStream(cmdSocket.getOutputStream());
                    out.writeInt(MSG_REMOTE_OK);
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    
                    // tell activity that send remote ok exception
                    // void to do
                    
                    Message msg = Message.obtain(null, NetCommunicationService.MSG_START_SERVICE);
                    serviceHandler.sendMessage(msg);
                }
                
                finally{
                    try {
                        cmdSocket.close();
                    }
                    catch (IOException e) {
                        LogUtil.log(this, "after send remote ok, close cmdSocket IOException");
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }}).start();
        
        
    }

    private void finishLocalTask() {
        // stop audio data socket
        
        if (audioDataSocket != null){
            try {
                audioDataSocket.close();
                audioDataSocket = null;
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        //stop video data socket
        if (videoDataSocket != null){
            try {
                videoDataSocket.close();
                videoDataSocket = null;
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        videoPlayer = null;
        
        // startcmdServer
        Message msg = Message.obtain(null, NetCommunicationService.MSG_START_SERVICE);
        serviceHandler.sendMessage(msg);
        
    }

    private void finishRemoteTask() {

        // stop audio data socket
        
        if (audioDataSocket != null){
            try {
                audioDataSocket.close();
                audioDataSocket = null;
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        //stop video data socket
        if (videoDataSocket != null){
            try {
                videoDataSocket.close();
                videoDataSocket = null;
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        videoPlayer = null;
        
        // startcmdServer
        Message msg = Message.obtain(null, NetCommunicationService.MSG_START_SERVICE);
        serviceHandler.sendMessage(msg);
        
    }

    private void doRegiste(Message msg) {
        activityMessager = msg.replyTo;
        
        // set
        if (videoPlayer!=null){
            videoPlayer.setMessenger(activityMessager);
        }
        
        
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        LogUtil.log(this, "onBind");
        return messenger.getBinder();
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        LogUtil.log(this, "onCreate");

        serviceHandler = new netCommandHandler();
        messenger = new Messenger(serviceHandler);

        Message msg = Message.obtain(null, NetCommunicationService.MSG_START_SERVICE);
        serviceHandler.sendMessage(msg);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        LogUtil.log(this, "onDestroy");

        Message msg = Message.obtain(null, NetCommunicationService.MSG_FINISH_SERVICE);
        serviceHandler.sendMessage(msg);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        LogUtil.log(this, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        LogUtil.log(this, "onUnbind");

        return super.onUnbind(intent);
    }

}
