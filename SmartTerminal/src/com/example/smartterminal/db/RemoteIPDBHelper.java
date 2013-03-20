package com.example.smartterminal.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RemoteIPDBHelper extends SQLiteOpenHelper {

	private static final String TABLE_NAME = "remote_ip";

	private static final String FIELD_IP = "ip";

	private static final String FIELD_NAME = "name";

	public RemoteIPDBHelper(Context context) {

		super(context, DBconstant.DATABASE_NAME, null,
				DBconstant.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// create table
		String sql = "Create table " + TABLE_NAME + "(" + FIELD_IP + " text,"
				+ FIELD_NAME + " text);";
		db.execSQL(sql);
		// insert inital sersor

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	// function get all remote IP
	public Cursor getAllRemoteIP(){
		SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME, null, null, null, null, null,  " _id desc");
        return cursor;
	}

	// function add one remote IP
	public long addOneRemoteIP(String ip, String name) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(FIELD_IP, ip);
		cv.put(FIELD_NAME, name);
		long row = db.insert(TABLE_NAME, null, cv);
		return row;
	}

	// function update one remote IP
	public void updateOneRemoteIP(String ip, String name){
		
	}

	// function delete one remote IP
	public void deleteOneRemoteIP(String ip, String name){
		
	}
}
