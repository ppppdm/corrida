package my.securityDemo.view;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SecurityGroupDBHelper extends SQLiteOpenHelper{

	private final static String DATABASE_NAME="sec_group_db";
    private final static int DATABASE_VERSION=1;
    
    private final static String TABLE_NAME="sec_group_info";
    public final static String FIELD_ID="_id"; 
    public final static String FIELD_NAME="name";
    public final static String FIELD_LIGHT1="light1";
    public final static String FIELD_LIGHT2="light2";
    public final static String FIELD_LIGHT3="light3";
    public final static String FIELD_LIGHT4="light4";
    
	public SecurityGroupDBHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public SecurityGroupDBHelper(Context context){
		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql="Create table "+TABLE_NAME+"("+FIELD_ID+" integer primary key autoincrement,"
        +FIELD_NAME+" text," + FIELD_LIGHT1+" integer," +FIELD_LIGHT2+" integer," 
        +FIELD_LIGHT3+" integer," +FIELD_LIGHT4+" integer);";
        db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	public Cursor query() {  
        SQLiteDatabase db = getWritableDatabase();  
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);  
        return c;  
    }
	
	public long insert(String name, int light1, int light2, int light3, int light4)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues(); 
        cv.put(FIELD_NAME, name);
        cv.put(FIELD_LIGHT1, light1);
        cv.put(FIELD_LIGHT2, light2);
        cv.put(FIELD_LIGHT3, light3);
        cv.put(FIELD_LIGHT4, light4);
        long row=db.insert(TABLE_NAME, null, cv);
        return row;
    }
	
	 public void update(int id,String name, int light1, int light2, int light3, int light4)
	 {
	        SQLiteDatabase db=this.getWritableDatabase();
	        String where=FIELD_ID+"=?";
	        String[] whereValue={Integer.toString(id)};
	        ContentValues cv=new ContentValues(); 
	        cv.put(FIELD_NAME, name);
	        cv.put(FIELD_LIGHT1, light1);
	        cv.put(FIELD_LIGHT2, light2);
	        cv.put(FIELD_LIGHT3, light3);
	        cv.put(FIELD_LIGHT4, light4);
	        db.update(TABLE_NAME, cv, where, whereValue);
	 }
	 
	 public Cursor selectByName(String name)
	 {
	        SQLiteDatabase db=this.getReadableDatabase();
	        Cursor cursor=db.query(TABLE_NAME, null, FIELD_NAME+" = '"+name+"'", null, null, null,  " _id desc");
	        return cursor;
	 }
}
