package com.example.loctest;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBase extends SQLiteOpenHelper{
	
		private int openedConnections=0;

	public DBase(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE wifi_db(_id INTEGER PRIMARY KEY, bssid VARCHAR, maxRSSI VARCHAR)";  
		db.execSQL(sql);  
		System.out.println("Table");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public void clean(SQLiteDatabase db) {
		String sql = "DELETE FROM wifi_db";  
		db.execSQL(sql);   
	}
	
	public int QueryItem(SQLiteDatabase db, String[] item){
		String sql = "select * from wifi_db where bssid=?";		
		Cursor cursor = db.rawQuery(sql, item);
		return cursor.getCount();
	}
	
}
