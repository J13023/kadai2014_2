package com.example.eventcalendar;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BankEdit extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.edit);
	    Button b1 = (Button)findViewById(R.id.button1);
	    Button b2 = (Button)findViewById(R.id.button2);
		EditText et1 = (EditText)findViewById(R.id.editText1);
		EditText et2 = (EditText)findViewById(R.id.editText2);
		EditText et3 = (EditText)findViewById(R.id.editText3);
	    et1.setText("");
		et2.setText("0");
		et3.setText("0");
	    b1.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
			Bundle extras = getIntent().getExtras();
			int rowid = extras.getInt("ID");
			EditText et1 = (EditText)findViewById(R.id.editText1);
			EditText et2 = (EditText)findViewById(R.id.editText2);
			EditText et3 = (EditText)findViewById(R.id.editText3);
			DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("comment", et1.getText().toString());
			values.put("nyukin", et2.getText().toString());
			values.put("syukkin", et3.getText().toString());
			long rowID =db.update("yokin_table", values, "id="+rowid, null);
			if(rowID == -1){
				db.close();
				throw new SQLException("FAiled to update row");
			}db.close();
			TextView tv = (TextView)findViewById(R.id.TextView01);
			tv.setBackgroundColor(Color.BLUE);
			tv.setText("DATAを登録しました");
			}
		});
	    b2.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				finish();
				Intent intent = new Intent(BankEdit.this,BankActivity.class);
				startActivity(intent);			}
		});


	    // TODO Auto-generated method stub
	}

}
