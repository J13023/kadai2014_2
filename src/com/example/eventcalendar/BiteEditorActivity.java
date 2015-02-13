package com.example.eventcalendar;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class BiteEditorActivity extends Activity implements OnClickListener {

	// Viewのインスタンス

		private EditText mWhereEditText = null;
		private TimePicker mStartTimeTextView = null;
		private TimePicker mEndTimeTextView = null;
		private Button mDiscardButton = null;
		private Button mSaveButton = null;

		// IntentでもらったデータベースID
		private long mId = 0;
		// 日付の文字列
		private String mDateString = null;
		static int result = 0;

		/**
		 * onCreate
		 * IDが０なら新規、１以上ならデータベースから情報を取得し格フィールドにセットする
		 */
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// リソースからViewを作成する
			setContentView(R.layout.biteeditor);
			// TextEditなどのビューを取得する
			mWhereEditText = (EditText)findViewById(R.id.kane);
			mStartTimeTextView = (TimePicker)findViewById(R.id.startTime);
			mEndTimeTextView = (TimePicker)findViewById(R.id.endTime);
			mDiscardButton = (Button)findViewById(R.id.discard);
			mSaveButton = (Button)findViewById(R.id.save);

			// 「OnClickListener」に「EventEditorActivity」をセットする
			mDiscardButton.setOnClickListener(this);
			mSaveButton.setOnClickListener(this);
			Intent intent = getIntent();
			// インテントのExtraからデータのIDを取得する
			mId  = intent.getLongExtra(EventInfo.ID,0);
			// インテントのExtraから日付を取得する
			mDateString = intent.getStringExtra("date");
			if(mId==0){
				// タップした日付で今の時刻からの予定としてデータを作成する
				//　引数でもらった日付をカレンダーに変換
				Calendar targetCal = EventInfo.toCalendar(mDateString);
				// 今の時刻を取得
				Calendar nowCal = new GregorianCalendar();
				// 開始時刻は今乃時刻
				//mStartTimeTextView.setText(EventInfo.timeFormat.format(nowCal.getTime()));
				// 時刻を１時間加算
				nowCal.add(Calendar.HOUR, 1);
				//　終了時刻は開始から１時間後
				//mEndTimeTextView.setText(EventInfo.timeFormat.format(nowCal.getTime()));
			}else{
				// データベースからデータを取得し、データの内容を編集エリアに設定する
				ContentResolver contentResolver = getContentResolver();
				String selection = EventInfo.ID+" = "+mId;
				Cursor c = contentResolver.query(EventCalendarActivity.RESOLVER_URI, null, selection, null, null);
				if(c.moveToNext()){
					mWhereEditText.setText(c.getString(c.getColumnIndex(EventInfo.BITE_KANE)));

					String startTime = c.getString(c.getColumnIndex(EventInfo.START_TIME));
					Calendar startCal = EventInfo.toCalendar(startTime);
					//mStartTimeTextView.setText(EventInfo.timeFormat.format(startCal.getTime()));
					String endTime = c.getString(c.getColumnIndex(EventInfo.END_TIME));
					Calendar endCal = EventInfo.toCalendar(endTime);
					//mEndTimeTextView.setText(EventInfo.timeFormat.format(endCal.getTime()));
					if(startCal.get(Calendar.HOUR_OF_DAY) == 0 &&
							startCal.get(Calendar.MINUTE) == 0){
						startCal.add(Calendar.DAY_OF_MONTH, 1);
						if(startCal.equals(endCal)){
							// 開始時刻が00:00で終了が翌日の00:00の場合
							// 終日の予定と判断する
							mStartTimeTextView.setVisibility(View.INVISIBLE);
							mEndTimeTextView.setVisibility(View.INVISIBLE);
						}
					}
				}
				c.close();
			}
			// 日時の編集用のリスナーを日時のテキストにセット
			mStartTimeTextView.setOnClickListener(new TimeOnClickListener(this));
			mEndTimeTextView.setOnClickListener(new TimeOnClickListener(this));
		}

		/**
		 * onClick
		 *  ボタンのどれかがタップされたときの処理
		 */
		public void onClick(View v) {
			TextView textView1 = (TextView)findViewById(R.id.textView3);
			if(v == mDiscardButton){
				// Discardボタンがタップされたら何もせずアクティビティを終了する
				Log.d("CALENDAR","Discard");
				finish();
			}else if(v == mSaveButton){
				// Saveボタンがタップされたら編集中のデータをデータベースに保存する
				ContentResolver contentResolver = getContentResolver();
				ContentValues values = new ContentValues();
				values.put(EventInfo.BITE_KANE, mWhereEditText.getText().toString());


				int start_hour =  mStartTimeTextView.getCurrentHour();
				int end_hour = mEndTimeTextView.getCurrentHour();

				 result = (end_hour - start_hour) * Integer.parseInt(mWhereEditText.getText().toString());
				 String str = String.valueOf(result);
				textView1.setText(str);
					/*values.put(EventInfo.START_TIME,EventInfo.toDBDateString(
							mStartDateTextView.getText().toString(),
							mStartTimeTextView.getText().toString()));
					values.put(EventInfo.END_TIME,EventInfo.toDBDateString(
							mEndDateTextView.getText().toString(),
							mEndTimeTextView.getText().toString()));*/

				if(mId == 0){
					//　IDが０なら新規なのでInsert
					contentResolver.insert(EventCalendarActivity.RESOLVER_URI, values);
					Log.d("CALENDAR","Insert:"+mId);
				}else{
					// IDが１以上なら更新なのでUpdate
					String where = EventInfo.ID+" = "+mId;
					contentResolver.update(EventCalendarActivity.RESOLVER_URI, values, where, null);
					Log.d("CALENDAR","Update: "+mId);
				}
				// 呼び出しもとに値を返すためのIntentを作成
				Intent intent = new Intent();
				// Extraに値をセット
				intent.putExtra(EventCalendarActivity.CHANGED,true);
				// 処理結果をセット
				setResult(RESULT_OK,intent);

				// 保存が完了したらアクティビティを終了する

			}
		}
		/**
		 * DateOnClickListener
		 *  日付の文字列にセットされるリスナー
		 */


			/**
			 * DatePickerDialogで設定が押されたとき呼び出されるメソッド
			 *
			 * @param int y 年
			 * @param int m 月
			 * @param int d 日
			 */
			public void onDateSet(DatePicker picker, int y, int m, int d) {

		}

		/**
		 * TimeOnClickListener
		 *  時刻の文字列にセットされるリスナー
		 */
		private class TimeOnClickListener implements OnClickListener{
			private Context mContext = null;
			public TimeOnClickListener(Context c){
				// Contextが必要なので、コンストラクタで渡して覚えておく
				mContext = c;
			}

			/**
			 * クリックされた時呼び出される
			 * @param View クリックされたビュー
			 */
			public void onClick(View v) {
				GregorianCalendar c = null;

					return;
				}

			}
		}






