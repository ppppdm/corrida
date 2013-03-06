package my.securityDemo.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class SettingActivity extends Activity {

	ListView listView;

	
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.setting);
	     
	     listView = (ListView)findViewById(R.id.listView2);
	     
	     SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.settinglist,
	        		new String[]{"title"},
	        		new int[]{R.id.title});
	     
	     listView.setAdapter(adapter);

	     listView.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					setTitle("点击第"+position+"个项目" + id);
					
					if(id == 0){
						Intent intent = new Intent();
						intent.setClass(SettingActivity.this, SetLightActivity.class);
						startActivity(intent);
					}
					
					if(id == 1){
						Intent intent = new Intent();
						intent.setClass(SettingActivity.this, SetSecurityGroupActivity.class);
						startActivity(intent);
					}
					
				}
	        	
	        });
	     
	 }
	 
	 private List<Map<String, Object>> getData() {
	        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	 
	        Map<String, Object> map = new HashMap<String, Object>();
	        map.put("title", "监视器");
	        list.add(map);
	 
	        map = new HashMap<String, Object>();
	        map.put("title", "工作组");
	        list.add(map);
	        
	        return list;
	    }
}
