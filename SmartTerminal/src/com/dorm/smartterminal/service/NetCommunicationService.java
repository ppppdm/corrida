package com.dorm.smartterminal.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Vector;

import com.dorm.smartterminal.global.util.LogUtil;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class NetCommunicationService extends Service {

	private netCommandHandler serviceHandler;
	private Messenger messenger;
	private ServerSocket serverSocket = null;

	private Boolean serverRunning = false;
	private Thread serviceThread = null;
	private Boolean taskRunning = false;

	private Vector<Socket> clientList = new Vector<Socket>();
	private Vector<Thread> taskList = new Vector<Thread>();

	static final int NET_COMMUNICATE_SERVER_TIMEOUT = 30;
	static final int NET_COMMUNICATE_SERVER_PORT = 6000;
	static final int NET_COMMUNICATE_SERVER_BACKLOG = 5;
	String TARGET_IP = null;

	public static final int MSG_START_SERVICE = 1;
	public static final int MSG_REMOTE_CONNECTED = 2;
	public static final int MSG_REMOTE_QUERY = 3;
	public static final int MSG_REMOTE_REFUSE = 4;
	public static final int MSG_LOCAL_QUERY = 5;
	public static final int MSG_LOCAL_REFUSE = 6;
	public static final int MSG_FINISH_SERVICE = 7;

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
				startRemoteTask(msg.arg1);
				break;
			case MSG_FINISH_SERVICE:
				finishService();
				break;
			case MSG_LOCAL_QUERY:
				startLocalTask(msg.arg1);
				break;
			default:
				super.handleMessage(msg);
			}
		}

	}

	// init the serverSocket listen and accept on port
	private void initService() {
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
						Socket client = serverSocket.accept();
						// set client to client queue()
						Log.v(tag, "new client!");
						clientList.add(client);

						int series = clientList.size() - 1;
						Message msg = Message.obtain(null,
								NetCommunicationService.MSG_REMOTE_CONNECTED);
						msg.arg1 = series;
						serviceHandler.sendMessage(msg);
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					serverRunning = false;
					LogUtil.log(this, "service ioexception");
					// should tell the main control serverSocket error

					// break via send msg by handler

				} catch (Exception e) {
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
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				// close server socket and finish service thread
				try {
					serverSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (serviceThread.isAlive()) {
					serverRunning = false;
					try {
						serviceThread.join();
					} catch (InterruptedException e) {
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
		if (!taskRunning) {
			taskRunning = true;

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

				}
			}).start();
			
			taskRunning = false;
		}

	}

	private void startLocalTask(int series) {
		// remote IP addr
		TARGET_IP = "192.168.1.201";
		
		if (!taskRunning) {
			taskRunning = true;

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					InetSocketAddress inet_addr = new InetSocketAddress(
							TARGET_IP, NET_COMMUNICATE_SERVER_PORT);
					Socket sock = new Socket();
					try {
						sock.connect(inet_addr);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}).start();
			
			taskRunning = false;
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

		Message msg = Message.obtain(null,
				NetCommunicationService.MSG_START_SERVICE);
		serviceHandler.sendMessage(msg);

		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		LogUtil.log(this, "onDestroy");

		Message msg = Message.obtain(null,
				NetCommunicationService.MSG_FINISH_SERVICE);
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
