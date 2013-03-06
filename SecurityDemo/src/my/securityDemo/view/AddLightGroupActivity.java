package my.securityDemo.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddLightGroupActivity extends Activity{

	EditText groupName;
	Button button_ok;
	Button button_cancel;
	Spinner spinner_light1;
	Spinner spinner_light2;
	Spinner spinner_light3;
	Spinner spinner_light4;
	int TURN_ON = 1;
	int TURN_OFF = 0;
	String TAG = "addlightgroup";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addlightgroup);
		
		groupName = (EditText)findViewById(R.id.editText_addlightgroup);
		 
		button_ok = (Button)findViewById(R.id.button_addlightgroupok);
		button_cancel = (Button)findViewById(R.id.button_addlightgroupcancel);
		
		spinner_light1 = (Spinner)findViewById(R.id.spinner1);
		spinner_light2 = (Spinner)findViewById(R.id.spinner2);
		spinner_light3 = (Spinner)findViewById(R.id.spinner3);
		spinner_light4 = (Spinner)findViewById(R.id.spinner4);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new String[] {"ON", "OFF"});
		spinner_light1.setAdapter(adapter);
		spinner_light2.setAdapter(adapter);
		spinner_light3.setAdapter(adapter);
		spinner_light4.setAdapter(adapter);
		
		
		button_ok.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SecurityGroupDBHelper dbhelper = new SecurityGroupDBHelper(getApplicationContext());
				
				String name = groupName.getText().toString();
				
				Log.v(TAG, "spinner light1 "+spinner_light1.getSelectedItemPosition());
				int light1 = (spinner_light1.getSelectedItemPosition()) == 0 ? TURN_ON : TURN_OFF;
				int light2 = (spinner_light2.getSelectedItemPosition()) == 0 ? TURN_ON : TURN_OFF;
				int light3 = (spinner_light3.getSelectedItemPosition()) == 0 ? TURN_ON : TURN_OFF;
				int light4 = (spinner_light4.getSelectedItemPosition()) == 0 ? TURN_ON : TURN_OFF;
				
				dbhelper.insert(name, light1, light2, light3, light4);
				dbhelper.close();
				
				Intent intent = new Intent();
				intent.setClass(AddLightGroupActivity.this, SetSecurityGroupActivity.class);
				startActivity(intent);
			}});
		
		button_cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(AddLightGroupActivity.this, SetSecurityGroupActivity.class);
				startActivity(intent);
			}});
		
		
	}
}
