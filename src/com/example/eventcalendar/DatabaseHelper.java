package com.example.eventcalendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	private static final String DATABASE_NAME = "mydata.db";
	private static final int DATABASE_VERSION = 4;
	private static final String TABLE_NAME = "yokin_table";
	private static final String ID ="id";
	private static final String COMMENT = "comment";
	private static final String NYUKIN  = "nyukin";
	private static final String SYUKKIN = "syukkin";
	private static final String ZANDAKA = "zandaka";
	private static final String HI = "hi";

	DatabaseHelper(Context  context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		String query = "create table "+ TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COMMENT + " TEXT,"
												+ NYUKIN + " INTEGER," + SYUKKIN + " INTEGER," + ZANDAKA + " INTEGER," + HI +" TEXT);";
		db.execSQL(query);

	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_NAME);
		onCreate(db);

	}


}
