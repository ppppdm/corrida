package test.work.testcontrolborad;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class RelayBoardService extends Service {

	private Looper mServiceLooper;
	private RelayBoradServiceHandler mServiceHandler;
	private RelayBoardServiceReader mServiceReader;
	/** For showing and hiding our notification. */
	NotificationManager mNM;
	/** Keeps track of all current registered clients. */
	ArrayList<Messenger> mClients = new ArrayList<Messenger>();
	/**
	 * Target we publish for clients to send messages to
	 * RelayBoradServiceHandler.
	 */

	Messenger mMessenger;

	/**
	 * Command to the service to register a client, receiving callbacks from the
	 * service. The Message's replyTo field must be a Messenger of the client
	 * where callbacks should be sent.
	 */
	static final int MSG_REGISTER_CLIENT = 1;

	/**
	 * Command to the service to unregister a client, ot stop receiving
	 * callbacks from the service. The Message's replyTo field must be a
	 * Messenger of the client as previously given with MSG_REGISTER_CLIENT.
	 */
	static final int MSG_UNREGISTER_CLIENT = 2;

	/**
	 * Command to service to set a new value. This can be sent to the service to
	 * supply a new value, and will be sent by the service to any registered
	 * clients with the new value.
	 */
	static final int MSG_SET_VALUE = 3;

	static final int MSG_START_SERVICE = 4;
	static final int MSG_FINISH_SERVICE = 5;
	static final int MSG_SEND_INFO = 6;
	static final int MSG_GET_INFO = 7;

	int RELAY_BORAD_PORT = 6000;
	String RELAY_BORAD_HOST = "192.168.1.110";

	SocketChannel mSocketChannel;
	static final String tag = "relayService";

	Thread mThreadRelayBoardReader;

	/**
	 * @author kissy Handler handle msg from activity with a looper because
	 *         network. Hande msg type below: MSG_START_SERVICE : open socket
	 *         and other init work. MSG_FINISH_SERVICE : close socket and other
	 *         finsh work.
	 */
	public class RelayBoradServiceHandler extends Handler {

		public RelayBoradServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MSG_START_SERVICE:
				initService();
				Log.v(tag, "msg start service");
				break;
			case MSG_FINISH_SERVICE:
				finishService();
				Log.v(tag, "msg finish service");
				break;
			case MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);
				Log.v(tag, "msg bind service");
				break;
			case MSG_UNREGISTER_CLIENT:
				mClients.remove(msg.replyTo);
				Log.v(tag, "msg unbind service");
				break;
			case MSG_SEND_INFO:
				// send info to relayboard
				// msg.arg1 is formated command, translate this command and
				// encode info
				byte[] byte_info = RelayBoardFrameTranslator
						.translateCommand(msg.arg1);
				ByteBuffer info = ByteBuffer.wrap(byte_info);
				// use SocketChannel send to relayboard
				try {
					mSocketChannel.write(info);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.v(tag, "socket channel write except!");
					e.printStackTrace();
				}
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	}

	public ByteBuffer EncodeControlCode() {
		ByteBuffer bb = ByteBuffer.allocate(1024);
		return bb;
	}

	/**
	 * @author kissy a loop thread for read net work information, work in a new
	 *         thread not mainThread. The loop end while mainThread call it
	 *         over.
	 */
	public class RelayBoardServiceReader implements Runnable {
		private boolean running = false;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// Thread has a loop by Judge flag running
			// in the while block should not call block method

			running = true;
			try {
				int nKeys = 0;
				Selector selector = Selector.open();
				mSocketChannel.register(selector, SelectionKey.OP_READ);

				while (running) {
					// we should use timeout because we don't want to block
					// after timeout while can Judge running flag.
					// timeout set to 10 sec
					long timeout = 10 * 1000;
					nKeys = selector.select(timeout);
					if (nKeys > 0) {
						Iterator<SelectionKey> i = selector.selectedKeys()
								.iterator();
						while (i.hasNext()) {
							SelectionKey s = i.next();
							printKeyInfo(s);
							if (s.isReadable()) {
								ByteBuffer buff = ByteBuffer.allocate(1024);
								((SocketChannel) s.channel()).read(buff);
								Log.v(tag, RelayBoardService.getHexString(buff
										.array()));
								for (int j = mClients.size() - 1; j >= 0; j--) {
									try {
										mClients.get(j).send(
												Message.obtain(null,
														MSG_GET_INFO, 0, 0));
									} catch (RemoteException e) {
										// The client is dead. Remove it from
										// the list;
										// we are going through the list from
										// back to front
										// so this is safe to do inside the
										// loop.
										mClients.remove(i);
									}

								}
							}
							i.remove();
						}
					}

					// sleep for debug
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		public boolean getRunning() {
			return this.running;
		}

		public void setRunning(boolean run_flag) {
			this.running = run_flag;
		}

		private void printKeyInfo(SelectionKey sk) {
			String s = new String();
			s = "Att: " + (sk.attachment() == null ? "no" : "yes");
			s += ", Read: " + sk.isReadable();
			s += ", Acpt: " + sk.isAcceptable();
			s += ", Cnct: " + sk.isConnectable();
			s += ", Wrt: " + sk.isWritable();
			s += ", Valid: " + sk.isValid();
			s += ", Ops: " + sk.interestOps();
			Log.v(tag, s);
		}

	}

	public static String getHexString(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;

	}

	/**
	 * @author kissy network inital should not in mainThread, so ues a new
	 *         thread to do it.
	 */
	public class initalService implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// network should not start in mainThread
			InetSocketAddress inet_addr = new InetSocketAddress(
					RELAY_BORAD_HOST, RELAY_BORAD_PORT);
			try {
				mSocketChannel = SocketChannel.open(inet_addr);
				// set nonblocking mode
				mSocketChannel.configureBlocking(false);
				Log.v("relayService", "SocketChannel open ok");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void initService() {

		// open socketchannel
		try {
			mSocketChannel = SocketChannel.open();
			// set nonblocking mode
			mSocketChannel.configureBlocking(false);

			// connect to relay borad
			InetSocketAddress inet_addr = new InetSocketAddress(
					RELAY_BORAD_HOST, RELAY_BORAD_PORT);
			if (mSocketChannel.connect(inet_addr)) {
				// void
			} else {
				for (int i = 0; i < 5; i++) {
					if (mSocketChannel.finishConnect()) {
						break;
					} else {
						Log.v("relayService",
								"sleep 500ms wait for connect complete");
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// do other init things
		if (mSocketChannel.isConnected()) {
			Log.v("relayService", "SocketChannel open ok");

			// Start another thread runing read from the socket.
			mServiceReader = new RelayBoardServiceReader();
			mThreadRelayBoardReader = new Thread(mServiceReader,
					"ServiceReader");
			mThreadRelayBoardReader.start();

		} else {
			Log.v("relayService", "SocketChannel not open");
		}
	}

	private void finishService() {
		if (mSocketChannel.isOpen()) {
			try {
				// close socket
				mSocketChannel.close();

				// stop the thread that running RelayBoardServiceReader
				if (mServiceReader.getRunning()) {
					mServiceReader.setRunning(false);
				}

				// wait for mThreadRelayBoardReader end
				try {
					mThreadRelayBoardReader.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.v(tag, "mThreadRelayBoardReader end");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Log.v(tag, "mSocketChannel not open yet!");
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mMessenger.getBinder();
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block. We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		// This thread handle messenger and send to relayborad.

		// start inital thread
		// initalService initalThread = new initalService();
		// Thread thread0 = new Thread(initalThread, "ServiceInit");
		// thread0.start();

		//
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new RelayBoradServiceHandler(mServiceLooper);
		mMessenger = new Messenger(mServiceHandler);

		// send start command
		Message msg = Message.obtain(null, RelayBoardService.MSG_START_SERVICE);
		mServiceHandler.sendMessage(msg);

		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// Display a notification about us starting.
		showNotification();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		// send finish service command
		Message msg = Message
				.obtain(null, RelayBoardService.MSG_FINISH_SERVICE);
		mServiceHandler.sendMessage(msg);
		Toast.makeText(getApplicationContext(), "onDestroy", Toast.LENGTH_SHORT)
				.show();
		super.onDestroy();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "onStartCommand",
				Toast.LENGTH_SHORT).show();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		// CharSequence text = getText(R.string.remote_service_started);

		// Set the icon, scrolling text and timestamp
		// Notification notification = new Notification(R.drawable.stat_sample,
		// text, System.currentTimeMillis());

		// The PendingIntent to launch our activity if the user selects this
		// notification
		// PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
		// new Intent(this, Controller.class), 0);

		// Set the info for the views that show in the notification panel.
		// notification.setLatestEventInfo(this,
		// getText(R.string.remote_service_label), text, contentIntent);

		// Send the notification.
		// We use a string id because it is a unique number. We use it later to
		// cancel.
		// mNM.notify(R.string.remote_service_started, notification);
	}
}
