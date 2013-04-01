package test.work.testcontrolborad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UserPresentReceiver  extends BroadcastReceiver{
	
	String tag = "userpresent";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.v(tag, "onRecive");
		// activity
		Intent acIntent = new Intent(context, TestControlBoradActivity.class);
		acIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 注意，必须添加这个标记，否则启动会失败
		context.startActivity(acIntent);

		// service
		Intent serIntent = new Intent("RealyBoardService");
		context.startService(serIntent);
	}

}
