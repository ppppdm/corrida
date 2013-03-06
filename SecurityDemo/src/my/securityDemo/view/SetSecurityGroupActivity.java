package my.securityDemo.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SetSecurityGroupActivity extends Activity{

	Button groupAdd;
	ListView groupListView;
	SecurityGroupDBHelper dbhelper;
	String TAG = "SG";
	int TURN_ON = 1;
	int TURN_OFF = 0;
	
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.setlightgroup);
	     
	     groupAdd = (Button)findViewById(R.id.lightgroup_add);
	     groupListView = (ListView)findViewById(R.id.listView_lightgroup);
	     
	     SimpleAdapter adapter = new SimpleAdapter(this, getData(),
					R.layout.setlightgroupvlist,
					new String[] { "groupname", "light1", "light2", "light3", "light4" }, 
					new int[] { R.id.groupname, R.id.light1, R.id.light2, R.id.light3, R.id.light4 });
	     groupListView.setAdapter(adapter);
	     
	     groupAdd.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(SetSecurityGroupActivity.this, AddLightGroupActivity.class);
				startActivity(intent);
			}});
	     
	     
	 }
	 
	 private List<Map<String, Object>> getData() {

			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

			// need to read data from database	
			SecurityGroupDBHelper dbhelper = new SecurityGroupDBHelper(
					getApplicationContext());

			Cursor c = dbhelper.query();
			c.moveToFirst();

			Log.v(TAG, "light count" + c.getCount());

			while (!c.isAfterLast()) {
				Map<String, Object> map = new HashMap<String, Object>();

				map.put("groupname", c.getString(1)+ " ");
				for(int i = 2; i < 6; i++){
					int lightstatus = c.getInt(i);
					String light = "light" + (i-1);
					
					if(lightstatus == TURN_ON){
						map.put(light, i-1 + " - ON |");
					}
					if(lightstatus == TURN_OFF){
						map.put(light, i-1 + " - OFF |");
					}
				}
			
				list.add(map);
				c.moveToNext();

			}

			dbhelper.close();

			return list;
	 }
}
