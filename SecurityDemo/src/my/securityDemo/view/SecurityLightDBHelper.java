package my.securityDemo.view;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SecurityLightDBHelper  extends SQLiteOpenHelper {
	

    private final static String DATABASE_NAME="sec_light_db";
    private final static int DATABASE_VERSION=1;
    
    private final static String TABLE_NAME="light_info";
    public final static String FIELD_ID="_id"; 
    public final static String FIELD_USED="use";
    public final static String FIELD_TAG="tag";
    
	public SecurityLightDBHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	public SecurityLightDBHelper(Context context){
		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql="Create table "+TABLE_NAME+"("+FIELD_ID+" integer primary key autoincrement,"
        +FIELD_USED+" text," + FIELD_TAG+" text);";
        db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
        String sql=" DROP TABLE IF EXISTS "+TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
	}
	
	public Cursor query() {  
        SQLiteDatabase db = getWritableDatabase();  
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);  
        return c;  
    }
	
	public long insert(String use, String tag)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues(); 
        cv.put(FIELD_USED, use);
        cv.put(FIELD_TAG, tag);
        long row=db.insert(TABLE_NAME, null, cv);
        return row;
    }
	
	 public void update(int id,String use, String tag)
	 {
	        SQLiteDatabase db=this.getWritableDatabase();
	        String where=FIELD_ID+"=?";
	        String[] whereValue={Integer.toString(id)};
	        ContentValues cv=new ContentValues(); 
	        cv.put(FIELD_USED, use);
	        cv.put(FIELD_TAG, tag);
	        db.update(TABLE_NAME, cv, where, whereValue);
	 }
	
	 public Cursor select()
	 {
	        SQLiteDatabase db=this.getReadableDatabase();
	        Cursor cursor=db.query(TABLE_NAME, null, null, null, null, null,  " _id desc");
	        return cursor;
	 }
	 
	 public void clear(){
	    	
	    	SQLiteDatabase db=this.getWritableDatabase();
	    	String sql=" DROP TABLE IF EXISTS "+TABLE_NAME;
	        db.execSQL(sql);

	    	
	}
}