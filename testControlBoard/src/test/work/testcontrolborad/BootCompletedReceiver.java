package test.work.testcontrolborad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		//activity
		Intent acIntent = new Intent(context, TestControlBoradActivity.class); 
		acIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //注意，必须添加这个标记，否则启动会失败 
	    context.startActivity(acIntent);
	    
	    //service
	    Intent serIntent = new Intent("RealyBoardService");
	    context.startService(serIntent);

	}

}
