package my.securityDemo.view;


import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SetLightDialog extends Dialog{
	
	TextView tv_title;
	EditText edit_use;
	EditText edit_tag;
	Button button_ok;
	Button button_cancel;
	RadioGroup use_radiogroup;
	
	String NEEDED = "needed";
	String NEVER = "never";
	String ALWAYS = "always";

	public SetLightDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.setContentView(R.layout.dialog_setlight);
	}

	public SetLightDialog(Context context, String title, String use, String tag) {
		super(context);
		// TODO Auto-generated constructor stub
		this.setContentView(R.layout.dialog_setlight);
		
		//tv_title = (TextView)findViewById(R.id.textView1);
		Log.v("dialog", "" + tv_title);
		//tv_title.setText(title);
		this.setTitle(title);
		
		Log.v("dialog", "use " + use);
		use_radiogroup = (RadioGroup)findViewById(R.id.radioGroup1);
		if(use.equals(NEEDED)){
			use_radiogroup.check(0);
		}
		if(use.equals(NEVER)){
			use_radiogroup.check(1);
		}
		if(use.equals(ALWAYS)){
			use_radiogroup.check(2);
		}
		
		edit_tag = (EditText)findViewById(R.id.editText1);
		edit_tag.setText(tag);
		
		button_ok = (Button)findViewById(R.id.button1);
		button_cancel = (Button)findViewById(R.id.button2);
	
	}
	
	Button getOkButton(){
		return button_ok;
	}
	
	Button getCancelButton(){
		return button_cancel;
	}

	ArrayList<String> getDialogInfo(){
		ArrayList<String> list = new ArrayList<String>();
		String use;
		switch(use_radiogroup.getCheckedRadioButtonId()){
		case 0:
			use = NEEDED;
		case 1:
			use = NEVER;
		case 2:
			use = ALWAYS;
		default:
				use = NEEDED;
		}
		
		list.add(use);
		Log.v("dialog",use + " " + edit_tag.getText().toString());
		list.add(edit_tag.getText().toString());
		return list;
	}
}
