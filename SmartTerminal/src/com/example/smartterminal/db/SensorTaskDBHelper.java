package com.example.smartterminal.db;

public class SensorTaskDBHelper extends SQLiteOpenHelper {

	private static final String TABLE_NAME = "sensor_task";

	private static final String FIELD_TASK_NAME = "task_name";

	private static final String FIELD_SENSOR_ID = "sensor_id";

	public SecurityLightDBHelper(Context context) {

		super(context, DBconstant.DATABASE_NAME, null,
				DBconstant.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// create table
		String sql = "Create table " + TABLE_NAME + "(" + FIELD_TASK_NAME
				+ " text" + FIELD_SENSOR_ID + " integer);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	// function create a sensor task
	public boolean createSensorTask(String task_name,
			ArrayList<Integer> sensor_list) {
		return false;
	}
	
	//functioin read all sensor task
	
	//function update one sensor task
	
	//function delete one sensor task
	
}
