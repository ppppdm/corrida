package my.securityDemo.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class SecurityLightService extends Service {

	String TAG = "sLs";
	int count;
	boolean running = false;
	Socket sock;
	OutputStream outputStream;
	InputStream inputStream;
	public Handler mHandler;
	IBinder binder = new MyBinder();
	byte lightStatus = 0;

	byte[] read_status = { (byte) 0x55, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
			(byte) 0xaa, (byte) 0xaa, (byte) 0x1, (byte) 0x1, (byte) 0xf,
			(byte) 0xb8, (byte) 0x16 };
	byte[] buff = new byte[100];

	static byte FRAME_START_CHAR = (byte) 0x55;
	static byte CONTROL_START_CHAR = (byte) 0xaa;
	static byte FRAME_END_CHAR = (byte) 0x16;
	static byte[] SUPER_ADDRESS = { (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
			(byte) 0xaa };
	static byte CMD_GET_STATUS = (byte) 0x22;
	static byte CMD_RET_STATUS = (byte) 0xA2;
	static byte CMD_OPEN_ClOSE_LIGHT = (byte) 0x11;

	static int LEN_OF_CMD_GET_STATUS = 1;
	static int LEN_OF_RET_CMD_GET_STATUS = 5;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onBind");
		return binder;
	}

	public class MyBinder extends Binder {

		SecurityLightService getService() {
			return SecurityLightService.this;
		}
	}

	public void onCreate() {
		Log.v(TAG, "service onCreate");
		running = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				while (running) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						byte[] dataCode = { (byte) 0x0f };
						byte[] info;
						byte[] reinfo;
						byte status;

						// sock connect
						sock = new Socket();// ("192.168.1.110", 6000);
						InetSocketAddress address = new InetSocketAddress(
								"192.168.1.110", 6000);
						sock.connect(address, 1000);

						Log.v(TAG, "service sock connect");

						outputStream = sock.getOutputStream();
						inputStream = sock.getInputStream();

						// write msg to outputstram
						info = EncodeControlInfo(CMD_GET_STATUS,
								LEN_OF_CMD_GET_STATUS, dataCode);
						outputStream.write(info);
						outputStream.flush();
						Log.v(TAG, "wirte out");

						// read msg from inputstream
						// reinfo = readControlInfo(inputStream);
						reinfo = readControlInfoByLength(inputStream,
								LEN_OF_RET_CMD_GET_STATUS);
						Log.v(TAG, "read info");
						Log.v(TAG, Bytes2HexString(reinfo));

						// analyses msg
						status = getSwitchStatusInfo(reinfo);
						// Log.v(TAG, "status " + status + ", 1 sw " +
						// isSwitchOpen(status, 1));
						// Log.v(TAG, "status " + status + ", 2 sw " +
						// isSwitchOpen(status, 2));
						// Log.v(TAG, "status " + status + ", 3 sw " +
						// isSwitchOpen(status, 3));
						// Log.v(TAG, "status " + status + ", 4 sw " +
						// isSwitchOpen(status, 4));

						sock.close();

						// send broadcast
						Intent intent = new Intent();
						intent.putExtra("i", count);
						// just put an fade value
						intent.putExtra("light", status);

						intent.setAction("android.intent.action.test");// action与接收器相同
						sendBroadcast(intent);

						count++;
						Log.v(TAG, "count " + count);

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.v(TAG, "exception");
					}

				}

				Log.v(TAG, "service end!");
			}
		}).start();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();

				mHandler = new Handler() {
					public void handleMessage(Message msg) {
						// process incoming messages here
						Log.v(TAG, "handleMessage ");
						lightStatus = getLightStatusFromMessage(msg);
					}
				};

				Log.v(TAG, "go to loop");
				Looper.loop();
			}

		}).start();
	}

	public void onDestroy() {
		this.running = false;
		Log.v(TAG, "service onDestroy");
	}

	public void onStart(Intent intent, int startId) {
		Log.v(TAG, "service onStart");
	}

	private byte getLightStatusFromMessage(Message msg) {
		// TODO Auto-generated method stub
		byte info = msg.getData().getByte("lightStatus");
		Log.v(TAG, "getLightStatusFromMessage " + info);
		return info;
	}
	
	public static void printHexString(String hint, byte[] b) {
		System.out.print(hint);
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase() + " ");
		}
		System.out.println("");
	}

	/**
	 * 
	 * @param b
	 *            byte[]
	 * @return String
	 */
	public static String Bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	public static int isSwitchOpen(byte infoCode, int switchNum) {
		// switchNum should be 1 to 4
		// infoCode should be 0 to 0xF
		if (switchNum < 1 || switchNum > 4) {
			//
			return 0;
		}

		// not check infoCode, just use the lower 4bit of infoCode
		return (infoCode >>> (byte) switchNum - 1) & 0x1;
	}

	public byte getSwitchStatusInfo(byte[] info) {
		// first should check weather is ret status cmd
		if (info[6] != CMD_RET_STATUS) {
			return 0;
		}

		// return switch status info
		return info[9];
	}

	public static byte[] readControlInfoByLength(InputStream is, int datalen) {
		byte info[] = new byte[datalen + 10];

		try {
			is.read(info);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return info;
	}

	public byte[] readControlInfo(InputStream is) {
		int len = 0;
		int re;
		byte[] buff = new byte[1024];
		byte[] info = null;

		try {
			// control info start with 0x55, end with 0x16
			while (is.read() != 0x55) {

			}
			buff[len] = 0x55;
			len++;

			while ((re = is.read()) != 0x16) {
				buff[len] = (byte) re;
				len++;
			}
			buff[len] = 0x16;
			len++;

			info = new byte[len];
			for (int i = 0; i < len; i++) {
				info[i] = buff[i];
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return info;
	}

	public static byte[] EncodeControlInfo(byte ControlCode,
			int DataCodeLenght, byte[] dataCode) {

		int len;
		int indexOfControlCodeEnd;
		byte[] info;
		byte cs;

		len = DataCodeLenght + 10;
		info = new byte[len];
		indexOfControlCodeEnd = 8 + DataCodeLenght - 1;
		cs = 0;

		setBytes(info, 0, 0, FRAME_START_CHAR);
		setBytesWithArray(info, 1, 4, SUPER_ADDRESS);
		setBytes(info, 5, 5, CONTROL_START_CHAR);
		setBytes(info, 6, 6, ControlCode);
		setBytes(info, 7, 7, (byte) DataCodeLenght);
		setBytesWithArray(info, 8, indexOfControlCodeEnd, dataCode);

		cs = checksum(info, 0, 8 + DataCodeLenght);
		setBytes(info, indexOfControlCodeEnd + 1, indexOfControlCodeEnd + 1, cs);
		setBytes(info, indexOfControlCodeEnd + 2, indexOfControlCodeEnd + 2,
				FRAME_END_CHAR);

		// System.out.println(info);
		// printBytesFormatedHex(info);
		return info;
	}

	public static byte checksum(byte[] bytes, int startIndex, int len) {

		int sum = 0;
		// System.out.printf("%x\n", sum);
		for (int i = 0; i < len; i++) {
			sum += bytes[startIndex + i] & 0xff;
			// System.out.printf("%x\n", sum);
		}
		// System.out.printf("%x\n", sum);
		return (byte) sum;
	}

	// index start from zero
	public static byte[] setBytes(byte[] bytes, int fromIndex, int toIndex,
			byte... args) {

		if (toIndex - fromIndex + 1 > args.length) {
			return null;
		}

		for (int i = fromIndex, j = 0; i <= toIndex; i++, j++) {
			bytes[i] = args[j];
		}

		return bytes;
	}

	public static byte[] setBytesWithArray(byte[] bytes, int fromIndex,
			int toIndex, byte[] args) {

		if (toIndex - fromIndex + 1 > args.length) {
			return null;
		}

		for (int i = fromIndex, j = 0; i <= toIndex; i++, j++) {
			bytes[i] = args[j];
		}
		return bytes;
	}

	public static void printBytesFormatedHex(byte[] bytes) {
		for (int i = 0; i < bytes.length; i++) {
			System.out.printf("%02x", bytes[i]);
		}
		System.out.println();
	}

}
