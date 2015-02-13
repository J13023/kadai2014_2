package com.example.eventcalendar;

import java.sql.SQLException;
import java.util.Calendar;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BankActivity extends Activity {
		static SQLiteDatabase mydb;
		private Integer[] data;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.bank);
			Button button1 = (Button)this.findViewById(R.id.Button01);
			Button button2 = (Button)this.findViewById(R.id.Button02);
			view();
			button1.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
						try {
							add();
							view();
						} catch (SQLException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						}
				}
			});
		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				Intent intent1 = new Intent(BankActivity.this,EventCalendarActivity.class);
				finish();
				startActivity(intent1);
				}
			});
		}
		private void add() throws SQLException{
			EditText editText1 = (EditText)findViewById(R.id.editText1);
			EditText editText2 = (EditText)findViewById(R.id.editText2);
			EditText editText3 = (EditText)findViewById(R.id.editText3);
			TextView textView1 = (TextView)findViewById(R.id.textView4);
			DatabaseHelper dbHelper = new DatabaseHelper(this);
			mydb = dbHelper.getWritableDatabase();
			String comment = editText1.getText().toString();
			String nyukin = editText2.getText().toString();
			String syukkin = editText3.getText().toString();
			String hi = Calendar.getInstance().getTime().toString();
			String zandaka = textView1.getText().toString();
			ContentValues values = new ContentValues();
			values.put("comment", comment);
			values.put("nyukin", nyukin);
			values.put("syukkin", syukkin);
			values.put("hi", hi);
			values.put("zandaka",zandaka);
			long rowID = mydb.insert("yokin_table", "", values);
			editText1.setText("");
			editText2.setText("");
			editText3.setText("");

			if(rowID == -1){
				mydb.close();

			}mydb.close();
		}
		private void view(){
			TextView textView1 = (TextView)findViewById(R.id.textView4);
			DatabaseHelper hlpr = new DatabaseHelper(getApplicationContext());
			mydb = hlpr.getWritableDatabase();
			ListView listView = (ListView)findViewById(R.id.ListView01);
			try{
				Cursor cr = mydb.rawQuery("Select * From yokin_table Order By id desc", null);
				cr.moveToFirst();
				if(cr.moveToFirst()){
					int a = cr.getInt(4)+ cr.getInt(2) - cr.getInt(3);
					String str = String.valueOf(a);
					textView1.setText(str);
				}
				if(cr.getCount() > 0){
					data = new Integer[cr.getCount()];
					ArrayAdapter<String>adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
					for(int cnt = 0; cnt<cr.getCount(); cnt++){
						data[cnt] = cr.getInt(0);
						adapter.add("ID:" + cr.getString(0) + "\n概要：" + cr.getString(1) +"\n入金：" + cr.getInt(2) + "\n出金：" + cr.getInt(3) +  "\n日付：" + cr.getString(5));
						cr.moveToNext();
						listView.setAdapter(adapter);

					}
				}else listView.setAdapter(null);
			}finally{
				mydb.close();
			}
			listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView <?> parent, View view, int position, long id){
					delete(data[(int)id]);
					return false;
				}

			});

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO 自動生成されたメソッド・スタブ
					finish();
					Intent intent = new Intent(BankActivity.this, BankEdit.class);
					intent.putExtra("ID", data[(int)id]);
					startActivity(intent);

				}

			});




		}
		private void delete(int id){
			DatabaseHelper dbHelper = new DatabaseHelper(this);
			SQLiteDatabase db  = dbHelper.getWritableDatabase();
			final Toast toast_s = Toast.makeText(this, "削除成功ID=" + id, Toast.LENGTH_LONG);
			final Toast toast_f = Toast.makeText(this, "削除失敗ID=" + id, Toast.LENGTH_LONG);
			int ret;
			try{
				ret = db.delete("yokin_table", "id = "+id, null);
				view();
			}finally{
				db.close();
			}
			if(ret == 1)toast_s.show();
			else toast_f.show();
		}


		public void onClick(View view){

		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.database, menu);
			return true;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// Handle action bar item clicks here. The action bar will
			// automatically handle clicks on the Home/Up button, so long
			// as you specify a parent activity in AndroidManifest.xml.
			return super.onOptionsItemSelected(item);
		}
	}

