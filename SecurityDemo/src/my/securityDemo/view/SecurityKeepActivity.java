package my.securityDemo.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SecurityKeepActivity extends Activity implements ServiceConnection {

	ListView listView;
	ArrayList<ImageView> light = new ArrayList<ImageView>();
	String TAG = "ska";
	int TURN_ON = 1;
	int TURN_OFF = 0;
	MyReceiver receiver;
	int[] status = new int[4];
	byte lightStauts = 0;
	byte lastLightStatus = 0;
	SecurityLightService myService;
	boolean mBound = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.securitykeep);

		listView = (ListView) findViewById(R.id.securityKeepListView);

		SimpleAdapter adapter = new SimpleAdapter(this, getData(),
				R.layout.settinglist, new String[] { "title" },
				new int[] { R.id.title });

		listView.setAdapter(adapter);

		ImageView light1 = (ImageView) findViewById(R.id.light1);
		ImageView light2 = (ImageView) findViewById(R.id.light2);
		ImageView light3 = (ImageView) findViewById(R.id.light3);
		ImageView light4 = (ImageView) findViewById(R.id.light4);

		light.add(light1);
		light.add(light2);
		light.add(light3);
		light.add(light4);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				SecurityGroupDBHelper dbhelper = new SecurityGroupDBHelper(
						getApplicationContext());
				ListView onClickListView = (ListView) parent;
				@SuppressWarnings("unchecked")
				HashMap<String, Object> map = (HashMap<String, Object>) onClickListView
						.getItemAtPosition(position);

				String name = (String) map.get("title");
				Cursor c = dbhelper.selectByName(name);
				Log.v(TAG,
						"count c " + c.getCount() + " colm "
								+ c.getColumnCount());
				c.moveToFirst();

				for (int i = 0; i < 4; i++) {
					int Status = c.getInt(i + 2);
					if (Status == TURN_ON) {
						light.get(i).setImageResource(R.drawable.o);
					} else {
						light.get(i).setImageResource(R.drawable.new_x);
					}
					status[i] = Status;
					
					//set lightStatus then send to service
					lightStauts = setLightInfo(lightStauts, i, Status);
				}
				
				//send lightStatus to service
				sendLightStatusToService(lightStauts);
				dbhelper.close();
			}
		});

		// 注册接收器
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.test");
		this.registerReceiver(receiver, filter);
		
		//bind service
		Intent intent = new Intent(this, SecurityLightService.class);
		bindService(intent, this, 0);
	}
	


	protected void sendLightStatusToService(byte sendinfo) {
		// TODO Auto-generated method stub
		if(mBound){
			Message msg = Message.obtain();
			Bundle data = new Bundle();
			data.putByte("lightStatus", sendinfo);
			msg.setData(data);
			myService.mHandler.sendMessage(msg);
			Log.v(TAG, "sendLightStatusToService " + sendinfo);
		} else{
			Log.v(TAG, "service hasn't bind");
		}
			
	}



	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mBound) {
            unbindService(this);
        }
	}



	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		SecurityGroupDBHelper dbhelper = new SecurityGroupDBHelper(
				getApplicationContext());

		Cursor c = dbhelper.query();
		c.moveToFirst();
		while (!c.isAfterLast()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", c.getString(1));
			list.add(map);
			c.moveToNext();
		}

		dbhelper.close();
		return list;
	}

	class MyReceiver extends BroadcastReceiver {
		// 自定义一个广播接收器
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//System.out.println("OnReceiver");
			Bundle bundle = intent.getExtras();
			int a = bundle.getInt("i");

			// 获取light信息
			byte lightInfo = bundle.getByte("light");
			Log.v(TAG, "lightInfo " + lightInfo);
			// analyse the light info
			// set view
			for (int i = 0; i < 4; i++) {
				if(status[i] == TURN_OFF){
					light.get(i).setImageResource(R.drawable.new_x);
					lightStauts = setLightInfo(lightStauts, i, TURN_OFF);
					continue;
				}else{
					if(isSwitchOpen(lightInfo, i+1) == 0){
						light.get(i).setImageResource(R.drawable.o);
						lightStauts = setLightInfo(lightStauts, i, TURN_OFF);
					}
					else  {
					light.get(i).setImageResource(R.drawable.spcae);
					lightStauts = setLightInfo(lightStauts, i, TURN_ON);
					}
				}
			}

			
			// send to board
			// start an service to control board
			sendToBoardLight(lightStauts);

			// 处理接收到的内容
			Log.v(TAG, "Keep get count " + a);
		}

		public MyReceiver() {
			System.out.println("MyReceiver");
			// 构造函数，做一些初始化工作，本例中无任何作用
		}

	}

	public byte setLightInfo(byte lightStatus, int lightNum, int status) {
		// lightNum from 0 to 3
		if (status == TURN_ON) {
			lightStatus |= (byte) (0x01 << lightNum);
		}
		if (status == TURN_OFF) {
			lightStatus &= (byte) ~(0x01 << lightNum);
		}

		return lightStatus;
	}

	public void sendToBoardLight(final byte status) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				boolean hadsend = false;
				Log.v(TAG, "sendToBoard run status " + status
						+" lastLightStatus " + lastLightStatus);
				if (status != lastLightStatus) {
					
					while (!hadsend) {
						try {
							byte light1, light2, light3, light4;
							//byte[] b = new byte[1024];
							Socket sock;

							sock = new Socket();//("192.168.1.110", 6000);
							InetSocketAddress address = new InetSocketAddress("192.168.1.110", 6000);
							sock.connect(address, 1000);
							Log.v(TAG, "sock connect");
							OutputStream outputStream = sock.getOutputStream();
							InputStream inputstream = sock.getInputStream();

							// 控制开关,首先控制全部关,再控制部分开
							byte[] dataCode_close = { 0x1f, (byte) 0x00,
									(byte) 0x00, (byte) 0x00, (byte) 0x00,
									(byte) 0x00, (byte) 0x00, (byte) 0x00,
									(byte) 0x00, (byte) 0x00, (byte) 0x00,
									(byte) 0x00, (byte) 0x00 };
							byte[] info3 = SecurityLightService
									.EncodeControlInfo(
											SecurityLightService.CMD_OPEN_ClOSE_LIGHT,
											dataCode_close.length,
											dataCode_close);
							outputStream.write(info3);
							outputStream.flush();
							//inputstream.read(b);
							byte info[] = SecurityLightService.readControlInfoByLength(inputstream, 3);
							Log.v(TAG, SecurityLightService.Bytes2HexString(info));

							light1 = (isSwitchOpen(status, 1) == 1) ? 0x00
									: (byte) 0xff;
							light2 = (isSwitchOpen(status, 2) == 1) ? 0x00
									: (byte) 0xff;
							light3 = (isSwitchOpen(status, 3) == 1) ? 0x00
									: (byte) 0xff;
							light4 = (isSwitchOpen(status, 4) == 1) ? 0x00
									: (byte) 0xff;
							byte[] dataCode2 = { 0x0f, (byte) 0xff, light1,
									light2, light3, light4, (byte) 0xff,
									(byte) 0xff, (byte) 0xff, (byte) 0xff,
									(byte) 0xff, (byte) 0xff, (byte) 0xff };
							byte[] info2 = SecurityLightService
									.EncodeControlInfo(
											SecurityLightService.CMD_OPEN_ClOSE_LIGHT,
											0x0d, dataCode2);
							outputStream.write(info2);
							outputStream.flush();
							//inputstream.read(b);
							SecurityLightService.readControlInfoByLength(inputstream, 3);
							
							sock.close();
							Log.v(TAG, "sock.close");
							hadsend = true;

						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}

				lastLightStatus = status;
			}

		}).start();
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

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		myService = ((SecurityLightService.MyBinder)service).getService();
		mBound = true;
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		mBound = false;
	}
}
