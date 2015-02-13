package com.example.eventcalendar;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class EventCalendarActivity extends Activity implements OnClickListener{
	// 一週間の日数
	private static final int DAYS_OF_WEEK = 7;
	// GridViewのインスタンス
	private GridView mGridView = null;
	// DateCellAdapterのインスタンス
	private DateCellAdapter mDateCellAdapter = null;
	// 現在注目している年月日を保持する変数
	private GregorianCalendar mCalendar = null;
	// カレンダーの年月を表示するTextView
	private TextView mYearMonthTextView = null;
	// ContentResolverのインスタンス
	private ContentResolver mContentResolver = null;
	// EventProviderのUri
	public static final Uri RESOLVER_URI = Uri.parse("content://com.example.eventcalendar.eventprovider");
	// EventDetailActivityを呼び出すためのrequestコード
	protected static final int EVENT_DETAIL = 2;
	// 前月ボタンのインスタンス
	private Button mPrevMonthButton = null;
	// 次月ボタンのインスタンス
	private Button mNextMonthButton = null;
	// Activityでデータベースが更新されたことを伝えるためのタグ
	public static final String CHANGED = "changed";


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mGridView = (GridView)findViewById(R.id.gridView1);
		// Gridカラム数を設定する
		mGridView.setNumColumns(DAYS_OF_WEEK);
		// DateCellAdapterのインスタンスを作成する
		mDateCellAdapter = new DateCellAdapter(this);
		// GridViewに「DateCellAdapter」をセット
		mGridView.setAdapter(mDateCellAdapter);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View v, int position,long id) {
				// カレンダーをコピー
				Calendar cal = (Calendar)mCalendar.clone();
				// positionから日付を計算
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.add(Calendar.DAY_OF_MONTH, position-cal.get(Calendar.DAY_OF_WEEK)+1);
				// 日付文字列を生成
				String dateString = EventInfo.dateFormat.format(cal.getTime());
				// Intent  を作成
				Intent intent = new Intent(EventCalendarActivity.this,EventDetailActivity.class);
				// 日付をExtraにセット
				intent.putExtra("date", dateString);
				// Activityを実行
				startActivityForResult(intent,EVENT_DETAIL);
			}
		});

		mYearMonthTextView = (TextView)findViewById(R.id.yearMonth);
		// 「GregorianCalendar」のインスタンスの作成
		mCalendar = new GregorianCalendar();
		// 年月の取得
		int year = mCalendar.get(Calendar.YEAR);
		int month = mCalendar.get(Calendar.MONTH)+1;
		// 年月のビューへの表示
		mYearMonthTextView.setText(year+"/"+month);
		// ContentResolverの取得
		mContentResolver = getContentResolver();
		// 前月ボタンにListenerを設定
		mPrevMonthButton = (Button)findViewById(R.id.prevMonth);
		mPrevMonthButton.setOnClickListener(this);
		// 次月ボタンにListenerを設定
		mNextMonthButton = (Button)findViewById(R.id.nextMonth);
		mNextMonthButton.setOnClickListener(this);

		Cursor c = mContentResolver.query(Uri.parse("content://com.example.eventcalendar.eventprovider"),null,null,null,null);
		Log.d("CALENDAR","Num of records:"+c.getCount());
		c.close();
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mi =getMenuInflater();
		mi.inflate(R.menu.menu,menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
		case R.id.item01:
		Intent intent1 = new Intent(EventCalendarActivity.this,BankActivity.class);
		finish();
		startActivity(intent1);
		break;
		}
		return true;

	}


	/**
	 * onClick
	 *  前月、次月ボタンでクリックされたとき呼び出される
	 */
	public void onClick(View v) {
		// 現在の注目している日付を当月の1日に変更する
		mCalendar.set(Calendar.DAY_OF_MONTH,1);
		if(v == mPrevMonthButton){
			// 1ヶ月減算する
			mCalendar.add(Calendar.MONTH, -1);
		}else if(v == mNextMonthButton){
			// 1ヶ月加算する
			mCalendar.add(Calendar.MONTH, 1);
		}
		mYearMonthTextView.setText(mCalendar.get(Calendar.YEAR)+"/"+(mCalendar.get(Calendar.MONTH)+1));
		mDateCellAdapter.notifyDataSetChanged();
	}

	/**
	 * onActivityResult
	 *  呼び出したEditorの処理が完了したとき呼び出される
	 * @param requestCode 起動時に指定したrequestCode
	 * @param resultCode 呼び出したActivityが終了時に設定した終了コード
	 * @param data 呼び出したActivityが終了時に設定したIntent
	 */
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		if(requestCode == EVENT_DETAIL && resultCode == RESULT_OK){
			if(data.getBooleanExtra(EventCalendarActivity.CHANGED,false)){
				// EVENT_DETAILの処理結果がOKでChangedがtrueなら、データベース更新を通知
				mDateCellAdapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * DateCellAdapterクラス
	 *  BaseAdapterを継承する。
	 */
	public class DateCellAdapter extends BaseAdapter {
		private static final int NUM_ROWS = 6;
		private static final int NUM_OF_CELLS = DAYS_OF_WEEK*NUM_ROWS;
		private LayoutInflater mLayoutInflater = null;
		/**
		 * コンストラクタではパラメタで受け取ったcontextを使用して
		 * 「LayoutInflater」のインスタンスを作成する。
		 * @param context アクティビティ
		 */
		DateCellAdapter(Context context){
			// getSystemServiceでContextからLayoutInflaterを取得
			mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		/**
		 * getCount
		 * 「NUM_OF_CELLS」 (42)を返す
		 */
		public int getCount() {
			return NUM_OF_CELLS;
		}
		/**
		 * getItem
		 * 必要ないのでnullを返す
		 */
		public Object getItem(int position) {
			return null;
		}
		/**
		 * getItemId
		 * 必要ないので0を返す
		 */
		public long getItemId(int position) {
			return 0;
		}
		/**
		 * getView
		 *  DateCellのViewを作成して返すためのメソッド
		 *  @param int position セルの位置
		 *  @param View convertView 前に使用したView
		 *  @param ViewGroup parent 親ビュー　ここではGridView
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = mLayoutInflater.inflate(R.layout.datecell,null);
			}
			// Viewの最小の高さを設定する
			convertView.setMinimumHeight(parent.getHeight()/NUM_ROWS-1);
			TextView dayOfMonthView = (TextView)convertView.findViewById(R.id.dayOfMonth);
			Calendar cal = (Calendar)mCalendar.clone();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.DAY_OF_MONTH, position-cal.get(Calendar.DAY_OF_WEEK)+1);
			dayOfMonthView.setText(""+cal.get(Calendar.DAY_OF_MONTH));
			if(position%7 == 0){
				dayOfMonthView.setBackgroundResource(R.color.red);
			}else if(position%7 == 6){
				dayOfMonthView.setBackgroundResource(R.color.blue);
			}else {
				dayOfMonthView.setBackgroundResource(R.color.gray);
			}
			TextView scheduleView = (TextView)convertView.findViewById(R.id.schedule);
			// Queryパラメータの設定
			String[] projection = {EventInfo.TITLE};
			String selection = EventInfo.START_TIME+" LIKE ?";
			String[] selectionArgs = {EventInfo.dateFormat.format(cal.getTime())+"%"};
			String sortOrder = EventInfo.START_TIME;
			// Queryの実行
			Cursor c = mContentResolver.query(RESOLVER_URI,projection,selection,selectionArgs,sortOrder);
			// 結果の文字列を作成しscheduleViewにセット
			// StringBuilder(追加可能な文字列クラス）
			StringBuilder sb = new StringBuilder();
			while(c.moveToNext()){

				// StringBuilderにスケジュールのタイトルを追加
				sb.append(c.getString(c.getColumnIndex(EventInfo.TITLE)));
				sb.append("\n");
			}
			c.close();
			// scheduleViewに予定のリストを追加
			scheduleView.setText( sb.toString());


			return convertView;
		}

	}


}