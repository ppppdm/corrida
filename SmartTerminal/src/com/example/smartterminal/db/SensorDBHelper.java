package com.example.smartterminal.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SensorDBHelper extends SQLiteOpenHelper {

	private static final String TABLE_NAME = "sensor";

	private static final String FIELD_ID = "id";

	private static final String FIELD_NAME = "name";

	private static final String FIELD_DELAY_ALARM = "delay_alarm";

	private static final String FIELD_DELAY_OPEN = "delay_open";

	private static final String FIELD_DELAY_CLOSE = "delay_close";

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// create table
		String sql = "Create table " + TABLE_NAME + "(" + FIELD_ID
				+ " integer primary key," + FIELD_NAME + " text,"
				+ FIELD_DELAY_ALARM + " integer DEFAULT 0," + FIELD_DELAY_OPEN
				+ "integer DEFAULT 0," + FIELD_DELAY_CLOSE
				+ "integer DEFAULT 0);";
		db.execSQL(sql);

		// insert inital sersor

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	public SecurityLightDBHelper(Context context) {

		super(context, DBconstant.DATABASE_NAME, null,
				DBconstant.DATABASE_VERSION);
	}

	// function get all sensor config

	// function update one sensor name

	// function update one sensor FIELD_DELAY_ALARM or FIELD_DELAY_OPEN or
	// FIELD_DELAY_CLOSE
	
}
