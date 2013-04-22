package com.dorm.smartterminal.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;
import com.dorm.smartterminal.global.util.LogUtil;
import com.dorm.smartterminal.netchat.activiy.NetChart;
import com.dorm.smartterminal.netchat.component.AudioPlayer;
import com.dorm.smartterminal.netchat.component.AudioRecorder;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class NetCommunicationService extends Service {

    private netCommandHandler serviceHandler = new netCommandHandler();
    private Messenger messenger;
    private ServerSocket serverSocket = null;

    private Messenger netChartHandler = null;

    private Socket PassiveClinet = null;
    private Socket InitiativeClient = null;

    private Boolean serverRunning = false;
    private Thread serviceThread = null;
    private Boolean taskRunning = false;

    private Vector<Socket> clientList = new Vector<Socket>();

    static final int NET_COMMUNICATE_SERVER_TIMEOUT = 30;
    static final int NET_COMMUNICATE_SERVER_PORT = 6000;
    static final int NET_COMMUNICATE_SERVER_BACKLOG = 5;
    String TARGET_IP = null;

    static final int NET_COMMUNICATE_AUDIO_PORT = 6001;

    public static final int MSG_START_SERVICE = 1;
    public static final int MSG_REMOTE_CONNECTED = 2;
    public static final int MSG_REMOTE_QUERY = 3;
    public static final int MSG_REMOTE_REFUSE = 4;
    public static final int MSG_LOCAL_QUERY = 5;
    public static final int MSG_LOCAL_REFUSE = 6;
    public static final int MSG_FINISH_SERVICE = 7;
    public static final int MSG_REGISTE = 8;

    final String tag = "netCommu";

    class netCommandHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // super.handleMessage(msg);
            switch (msg.what) {
            case MSG_START_SERVICE:
                initService();
                break;
            case MSG_REMOTE_CONNECTED:
            case MSG_REMOTE_QUERY:
                startRemoteTask(msg.arg1);
                break;
            case MSG_REMOTE_REFUSE:
                finishRemoteTask(msg);
            case MSG_FINISH_SERVICE:
                finishService();
                break;
            case MSG_LOCAL_QUERY:
                startLocalTask(msg);
                break;
            case MSG_LOCAL_REFUSE:
                finishLocalTask();
                break;
            case MSG_REGISTE:
                doRegiste(msg);
            default:
                super.handleMessage(msg);
            }
        }

    }

    // init the serverSocket listen and accept on port
    private void initService() {
        
        // init and start audioPlayer service
        try {
            ServerSocket audioSocket = new ServerSocket(NET_COMMUNICATE_AUDIO_PORT);
            AudioPlayer audioPlayer = new AudioPlayer();
            audioPlayer.initAudioPlayer(audioSocket);
            audioPlayer.run();
        }
        catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        //init and start videoPlayer service
        
        
        
        // create new thread for server to accept
        serviceThread = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    serverSocket = new ServerSocket(NET_COMMUNICATE_SERVER_PORT);
                    // serverSocket.setSoTimeout(NET_COMMUNICATE_SERVER_TIMEOUT);
                    serverRunning = true;
                    while (serverRunning) {
                        PassiveClinet = serverSocket.accept();

                        DataInputStream in = new DataInputStream(PassiveClinet.getInputStream());
                        // DataOutputStream out = new
                        // DataOutputStream(client.getOutputStream());

                        while (true) {
                            try {

                                int cmd = in.readInt();

                                if (cmd == MSG_REMOTE_QUERY) {
                                    Message msg = Message.obtain(null, NetCommunicationService.MSG_REMOTE_CONNECTED);
                                    serviceHandler.sendMessage(msg);

                                }
                                else if (cmd == MSG_REMOTE_REFUSE) {
                                    Message msg = Message.obtain(null, NetCommunicationService.MSG_REMOTE_REFUSE);
                                    serviceHandler.sendMessage(msg);
                                    break;
                                }
                                else {
                                    break;
                                }
                            }
                            catch (IOException e) {
                                LogUtil.log(this, " client read io exception");
                                break;
                            }
                        }

                        PassiveClinet = null;
                    }

                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    serverRunning = false;
                    LogUtil.log(this, "service ioexception");
                    // should tell the main control serverSocket error

                    // break via send msg by handler

                }
                catch (Exception e) {
                    e.printStackTrace();
                    serverRunning = false;
                    LogUtil.log(this, "other exception");
                }

                Log.v(tag, "net communicta server socket closed!");
            }
        });

        serviceThread.start();
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
                    serverSocket.close();
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (serviceThread.isAlive()) {
                    serverRunning = false;
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
        // create new thread for remote task

        // start activity
        this.startActivity(new Intent(this, NetChart.class));

        

        String remoteIP = PassiveClinet.getInetAddress().getHostAddress();

        try {
            
            // start audio Recorder and connect to remote audio Server
            Socket audioSocket = new Socket(remoteIP, NET_COMMUNICATE_AUDIO_PORT);

            AudioRecorder audioRecorder = new AudioRecorder();
            audioRecorder.initAudioRecorder(audioSocket);
            audioRecorder.run();

        }
        catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void startLocalTask(Message msg) {
        // remote IP addr
        String remoteIP = msg.getData().getString("IP");
        TARGET_IP = remoteIP;

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                InetSocketAddress inet_addr = new InetSocketAddress(TARGET_IP, NET_COMMUNICATE_SERVER_PORT);
                InitiativeClient = new Socket();
                try {
                    InitiativeClient.connect(inet_addr);

                    DataOutputStream out = new DataOutputStream(InitiativeClient.getOutputStream());
                    out.writeInt(MSG_REMOTE_QUERY);

                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                // connect remote audioplayer service
                try {
                    Socket audioSocket = new Socket(TARGET_IP,NET_COMMUNICATE_AUDIO_PORT);
                    
                    // start audioRecorder
                    AudioRecorder audioRecorder = new AudioRecorder();
                    audioRecorder.initAudioRecorder(audioSocket);
                    audioRecorder.run();
                    
                }
                catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private void finishLocalTask() {
        try {
            DataOutputStream out = new DataOutputStream(InitiativeClient.getOutputStream());
            out.writeInt(MSG_REMOTE_REFUSE);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        taskRunning = false;
    }

    private void finishRemoteTask(Message msg) {

    }

    private void doRegiste(Message msg) {
        netChartHandler = msg.replyTo;
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
