package com.example.loctest;

import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {


	private WifiManager wifiManager;	
	private Button infoBtn;
	private TextView tView;

	private SQLiteDatabase sqldbDatabase;
	public String DB_NAME = "RSSIsql.db";  
	public String DB_TABLE = "num";  
	public int DB_VERSION = 1;  
	final DBase dBase = new DBase(this, DB_NAME, null, DB_VERSION); 
	final ContentValues cValues  = new ContentValues();

	final Handler handler = new Handler();  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);


		infoBtn = (Button)findViewById(R.id.infoBtn);
		tView = (TextView)findViewById(R.id.textView1);

		sqldbDatabase = dBase.getReadableDatabase();

		dBase.clean(sqldbDatabase);

		infoBtn.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View view){

				wifiManager.startScan();
				handler.post(myRunnable);  
			}
		});
	}

	private Runnable myRunnable= new Runnable() {  
		@Override
		public void run() {   
			try {
				if (WifiManager.WIFI_STATE_ENABLED==wifiManager.getWifiState()){

					List<ScanResult> list = wifiManager.getScanResults();	

					if(list != null){
						for (ScanResult scanResult:list) {
							cValues.put("bssid", scanResult.BSSID);  
							cValues.put("maxRSSI", scanResult.level);
							String[] paraStrings = {scanResult.BSSID};


							if(1 == dBase.QueryItem(sqldbDatabase, paraStrings)){
								sqldbDatabase.update("wifi_db", cValues, "bssid=?", paraStrings);
							}
							else if(0 == dBase.QueryItem(sqldbDatabase, paraStrings)){

								sqldbDatabase.insert("wifi_db", null, cValues);// 插入数据

							}
							else {
								System.out.println("ERROR"+scanResult.BSSID+" "+scanResult.level);
							}
						}
					}
					list.clear();
					Toast.makeText(MainActivity.this, "OK 2" ,Toast.LENGTH_SHORT).show(); 
				} else {
					tView.setText("Wifi was not open.");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}        					
			handler.postDelayed(this, 2000);// 50是延时时长  
			updatelistview();	
			wifiManager.startScan();
		}
	};   

	public void updatelistview() {
		ListView lView = (ListView)findViewById(R.id.lView);

		final Cursor crCursor = sqldbDatabase.query("wifi_db", null, null, null, null, null, null);

//		System.out.println(crCursor.getCount());
		String[] columnNameStrings = crCursor.getColumnNames();


		@SuppressWarnings("deprecation")
		ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.layout, crCursor, columnNameStrings, new int[]{R.id.tv1,R.id.tv2,R.id.tv3});

		lView.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override        
	protected void onDestroy() {         
		handler.removeCallbacks(myRunnable);   
		sqldbDatabase.close();
		super.onDestroy();         
	}
}
