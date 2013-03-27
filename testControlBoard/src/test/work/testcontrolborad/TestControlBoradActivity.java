package test.work.testcontrolborad;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TestControlBoradActivity extends Activity {

	Button button;
	TextView textView;
	
	Button startButton;
	Button endButton;
	Intent mServiceIntent;
	Button bindButton;
	Button unbindButton;
	Button openAlarmButton;
	
	EditText editText;
	TextView alarmTextView;

	byte[] read_arg = { 0x55, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
			(byte) 0xaa, (byte) 0xaa, 0x00, 0x01, 0x01, (byte) 0xa9, 0x16 };

	byte[] open_lights = { 0x55, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
			(byte) 0xaa, (byte) 0xaa, 0x11, 0x0d, 0x0f, 0x00, 0x00, 0x00, 0x00,
			(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
			(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xcc, 0x16 };
	
	byte[] open_lights2 = { 0x55, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
			(byte) 0xaa, (byte) 0xaa, 0x11, 0x02,  0x01, 0x00, (byte)0xbb, 0x16 };

	byte[] read_status = { 0x55, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
			(byte) 0xaa, (byte) 0xaa, 0x1, 0x1, 0xf, (byte) 0xb8, 0x16 };

	byte[] ax = { (byte) 0x55, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
			(byte) 0xAA, (byte) 0xAA, (byte) 0x22, (byte) 0x01, (byte) 0x0F,
			(byte) 0xD9, (byte) 0x16 };

	// 55 00 00 00 00 AA A2 05 0F 0F 00 00 00 C4 16
	// 55 00 00 00 00 AA A2 05 0F 0E 00 00 00 C3 16
	// 55 00 00 00 00 AA A2 05 0F 06 00 00 00 BB 16

	static byte FRAME_START_CHAR = (byte) 0x55;
	static byte CONTROL_START_CHAR = (byte) 0xaa;
	static byte FRAME_END_CHAR = (byte) 0x16;
	static byte[] SUPER_ADDRESS = { (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
			(byte) 0xaa };
	static byte CMD_GET_STATUS = (byte)0x22;
	static byte CMD_RET_STATUS = (byte)0xA2;
	static byte CMD_OPEN_ClOSE_LIGHT = (byte)0x11;
	
	/** Messenger for communicating with service. */
    Messenger mService = null;
	/** Some text view we are using to show state information. */
    TextView mCallbackText;
    String tag = "boardActivity";
	/**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        	
            switch (msg.what) {
            	case RelayBoardService.MSG_GET_INFO:
                    //mCallbackText.setText("Received from service: " + msg.arg1);
            		Bundle data = msg.getData();
            		byte[] frame = data.getByteArray("info");
                	alarmTextView.setText(RelayBoardService.getHexString(frame));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
	
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    
    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mService = new Messenger(service);
            //mCallbackText.setText("Attached.");

            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                Message msg = Message.obtain(null,
                		RelayBoardService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
                
                // Give it some value as an example.
                msg = Message.obtain(null,
                		RelayBoardService.MSG_SET_VALUE, this.hashCode(), 0);
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }
            
            // As part of the sample, tell the user what happened.
            Toast.makeText(getApplicationContext(), R.string.remote_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            //mCallbackText.setText("Disconnected.");

            // As part of the sample, tell the user what happened.
            Toast.makeText(getApplicationContext(), R.string.remote_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };
    
    private OnClickListener mBindListener = new OnClickListener() {
        public void onClick(View v) {
            doBindService();
        }
    };

    private OnClickListener mUnbindListener = new OnClickListener() {
        public void onClick(View v) {
            doUnbindService();
        }
    };
    boolean mIsBound;
    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        //mCallbackText.setText("Binding.");
    }
    
    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null,
                    		RelayBoardService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                }
            }
            
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
            //mCallbackText.setText("Unbinding.");
        }
    }

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		textView = (TextView) findViewById(R.id.textView1);
		button = (Button) findViewById(R.id.button1);

		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Button Click", Toast.LENGTH_SHORT).show();
			}
			
		});
		
		mServiceIntent = new Intent(this, RelayBoardService.class);
		startButton = (Button)findViewById(R.id.button_start_server);
		endButton = (Button)findViewById(R.id.button_end_service);
		
		startButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// start RelayBoardService
				startService(mServiceIntent);
			}
			
		});
		
		endButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// start RelayBoardService
				stopService(mServiceIntent);
			}
			
		});
		
		
		// bind button
		bindButton = (Button)findViewById(R.id.button_bind_service);
		unbindButton = (Button)findViewById(R.id.button_unbind_service);
		bindButton.setOnClickListener(mBindListener);
		unbindButton.setOnClickListener(mUnbindListener);
		
		
		editText = (EditText)findViewById(R.id.open_alarm);
		openAlarmButton = (Button)findViewById(R.id.open_alarm);
		
		openAlarmButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//get editText content
				int light_num = Integer.valueOf(editText.getText().toString()).intValue();
				
				//send msg to service
				if (mService == null){
					return;
				}
				 try {
		                Message msg = Message.obtain(null,
		                		RelayBoardService.MSG_SEND_INFO);
		                msg.arg1 = RelayBoardFrameTranslator.formateCommand(RelayBoardFrameTranslator.OPEN_ONE_LIGHT,
		                		 light_num);
		                mService.send(msg);
		                
		            } catch (RemoteException e) {
		               Log.v(tag, "remote exception");
		            }
				
			}
			
		});
		
		alarmTextView = (TextView)findViewById(R.id.textView_alarm_status);
		
		
		/*
		button.setOnClickListener(new OnClickListener() {

			private String TAG = "cb";

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Log.v(TAG, "button click");
							Socket sock = new Socket("192.168.1.110", 6000);
							Log.v(TAG, "sock connect");
							OutputStream outputStream = sock.getOutputStream();
							InputStream inputstream = sock.getInputStream();

							byte []b = new byte[1024];
							byte[] dataCode = { (byte) 0x0f };
							byte[] info = EncodeControlInfo(CMD_GET_STATUS, 1,
									dataCode);

							outputStream.write(info);
							outputStream.flush();

							Log.v(TAG, "write out ax");

							
							

							Log.v(TAG, "before read ax");
							//redLen = inputstream.read(b);
						    byte[] reinfo = readControlInfo(inputstream);

							Log.v(TAG, Bytes2HexString(reinfo));
							
							byte status = getSwitchStatusInfo(reinfo);
							
							Log.v(TAG, "status " + status + ", 1 sw " + isSwitchOpen(status, 1));
							Log.v(TAG, "status " + status + ", 2 sw " + isSwitchOpen(status, 2));
							Log.v(TAG, "status " + status + ", 3 sw " + isSwitchOpen(status, 3));
							Log.v(TAG, "status " + status + ", 4 sw " + isSwitchOpen(status, 4));
							
							//open light
							
							
							//控制开关,首先控制全部关,再控制部分开
							byte []dataCode_close = {0x1f, (byte) 0x00, (byte) 0x00, (byte) 0x00,
									(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
									(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
							byte []info3 = EncodeControlInfo(CMD_OPEN_ClOSE_LIGHT, 0x0d, dataCode_close);
							outputStream.write(info3);
							outputStream.flush();
							Log.v(TAG, "write close light");
							inputstream.read(b);
							Log.v(TAG, "read b");
							
							byte light1, light2, light3, light4;
							light1 = (isSwitchOpen(status, 1) == 1)? 0x00 : (byte)0xff;
							light2 = (isSwitchOpen(status, 2) == 1)? 0x00 : (byte)0xff;
							light3 = (isSwitchOpen(status, 3) == 1)? 0x00 : (byte)0xff;
							light4 = (isSwitchOpen(status, 4) == 1)? 0x00 : (byte)0xff;
							
							byte []dataCode2 = {0x0f, (byte) 0xff, light1, light2, light3, light4,
									(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff
									, (byte) 0xff, (byte) 0xff, (byte) 0xff};
							byte [] info2 = EncodeControlInfo(CMD_OPEN_ClOSE_LIGHT, 0x0d, dataCode2);
							
							
							
							outputStream.write(info2);
							outputStream.flush();
							Log.v(TAG, "write open light");
							
							inputstream.read(b);
							sock.close();
							Log.v(TAG, "sock close");

						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
			}
		});*/
	}
	
	public static int isSwitchOpen(byte infoCode, int switchNum){
		//switchNum should be 1 to 4
		//infoCode should be 0 to 0xF
		if(switchNum < 1 || switchNum > 4){
			//
			return 0;
		}
		
		//not check infoCode, just use the lower 4bit of infoCode
		return (infoCode >>> (byte)switchNum - 1) & 0x1;
	}
	
	public byte getSwitchStatusInfo(byte[] info){
		//first should check weather is ret status cmd
		if(info[6] != CMD_RET_STATUS){
			return 0;
		}
		
		//return switch status info	
		return info[9];
	}
	
	public byte[] readControlInfo(InputStream is){
		int len = 0;
		int re;
		byte[] buff = new byte[1024];
		byte[] info = null;
		
		try {
			//control info start with 0x55, end with 0x16
			while(is.read() != 0x55){
				
			}
			buff[len] = 0x55;
			len++;
			
			while((re = is.read()) !=0x16){
				buff[len] = (byte)re;
				len++;
			}
			buff[len] = 0x16;
			len++;
			
			info = new byte[len];
			for(int i = 0; i < len; i++){
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
		printBytesFormatedHex(info);
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
}
