package com.dorm.smartterminal.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

public class NetCommunicationService extends Service {

    private netCommandHandler serviceHandler;
    private Messenger messenger;
    private ServerSocket serverSocket = null;
    
    private Boolean serverRunning     = false;

    static final int NET_COMMUNICATE_SERVER_TIMEOUT   = 30;
    static final int NET_COMMUNICATE_SERVER_PORT      = 6000;
    static final int NET_COMMUNICATE_SERVER_BACKLOG   = 5;

    static final int MSG_START_SERVICE                = 1;
    static final int MSG_REMOTE_CONNECTED             = 2;
    static final int MSG_REMOTE_QUERY                 = 3;
    static final int MSG_REMOTE_REFUSE                = 4;
    static final int MSG_LOCAL_QUERY                  = 5;
    static final int MSG_LOCAL_REFUSE                 = 6;
    static final int MSG_FINISH_SERVICE               = 7;

    class netCommandHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // super.handleMessage(msg);
            switch (msg.what) {
            case MSG_START_SERVICE:
                initServerSocket();
                break;
            case MSG_REMOTE_CONNECTED:
                startRemoteTask();
                break;
            default:
                super.handleMessage(msg);
            }
        }

    }

    
    
    // init the serverSocket listen and accept on port
    private void initServerSocket() {
        //create new thread for server to accept 
        new Thread(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    serverSocket = new ServerSocket(NET_COMMUNICATE_SERVER_PORT);
                    serverSocket.setSoTimeout(NET_COMMUNICATE_SERVER_TIMEOUT);
                    serverRunning = true;
                    while(serverRunning){
                        Socket client = serverSocket.accept();
                        // set client to client queue()
                        
                        Message msg = Message.obtain(null, NetCommunicationService.MSG_REMOTE_CONNECTED);
                        serviceHandler.sendMessage(msg);
                    }
                    
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    serverRunning = false;
                    // should tell the main control serverSocket error 
                    // break via send msg by handler
                    
                }
            }}).start();
    }
    
    // remote task for get remote cmd 
    private void startRemoteTask(){
        // create new thread for remote task
        new Thread(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                
            }}).start();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return messenger.getBinder();
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        serviceHandler = new netCommandHandler();
        messenger = new Messenger(serviceHandler);

        Message msg = Message.obtain(null, NetCommunicationService.MSG_START_SERVICE);
        serviceHandler.sendMessage(msg);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        serverRunning = false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

}
