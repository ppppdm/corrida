package my.securityDemo.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class SetLightActivity extends Activity {

	ListView listView;
	String TAG = "setlight";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		listView = (ListView) findViewById(R.id.listView2);

		SimpleAdapter adapter = new SimpleAdapter(this, getData(),
				R.layout.setlightvlist,
				new String[] { "title", "info", "tag" }, new int[] {
						R.id.title, R.id.info, R.id.tag });

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.v(TAG, "light item click " + id);
				ListView onClickListView = (ListView) parent;
				@SuppressWarnings("unchecked")
				HashMap<String, Object> map = (HashMap<String, Object>) onClickListView
						.getItemAtPosition(position);
				String title = (String) map.get("title");
				String info = (String) map.get("info");
				String tag = (String) map.get("tag");

				Log.v(TAG, "light item click " + title + " " + info + " " + tag);
				Toast.makeText(getApplicationContext(), title,
						Toast.LENGTH_LONG).show();
				final long light_id = id + 1;

				// Dialog dialog = new Dialog(SetLightActivity.this);
				// dialog.setContentView(R.layout.dialog_setlight);
				// dialog.setTitle(title);
				// EditText textuse =
				// (EditText)findViewById(R.id.textEditor_use);
				// textuse.setText(info);

				// EditText texttag =
				// (EditText)findViewById(R.id.textEditor_tag);
				// texttag.setTag(tag);
				// dialog.show();

				final SetLightDialog dialog = new SetLightDialog(
						SetLightActivity.this, title, info, tag);
				dialog.show();

				dialog.getOkButton().setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						ArrayList<String> list = dialog.getDialogInfo();
						dialog.dismiss();
						SecurityLightDBHelper dbhelper = new SecurityLightDBHelper(
								getApplicationContext());
						dbhelper.update((int)light_id, list.get(0), list.get(1));
						dbhelper.close();
					}
				});
				
				dialog.getCancelButton().setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
					
				});

			}
		});

	}

	private List<Map<String, Object>> getData() {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		// need to read data from database
		
		SecurityLightDBHelper dbhelper = new SecurityLightDBHelper(
				getApplicationContext());

		// first insert light to db
		// dbhelper.insert("needed", "");
		// dbhelper.insert("needed", "");
		// dbhelper.insert("needed", "");
		// dbhelper.insert("needed", "");

		// dbhelper.clear();
		// dbhelper.close();

		Cursor c = dbhelper.query();
		c.moveToFirst();

		Log.v(TAG, "light count " + c.getCount());

		while (!c.isAfterLast()) {
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("title", "Light" + c.getInt(0) + " ");
			Log.v(TAG, "Light" + c.getInt(0));
			map.put("info", c.getString(1));
			map.put("tag", c.getString(2));
			list.add(map);
			c.moveToNext();

		}
		Log.v(TAG, "list size " + list.size());
		
		dbhelper.close();

		return list;
	}
}
