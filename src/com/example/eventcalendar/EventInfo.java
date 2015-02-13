package com.example.eventcalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.provider.BaseColumns;

public class EventInfo {
	// データベース名
	public final static String DB_NAME = "events";
	// フィールド名
	public final static String ID = BaseColumns._ID;
	public final static String TITLE = "title";
	public final static String CONTENT = "content";
	public final static String WHERE = "gd_where";
	public final static String END_TIME = "gd_when_endTime";
	public final static String START_TIME = "gd_when_startTime";
	public final static String BITE_KANE = "bite_kane";
	//public final static String BITE_MONEY = "bite_money";
	public final static String DATE_MONAY ="date_monay";
	//　日付のみのフォーマット
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	// 時刻のみのフォーマット
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	// RFC822に従ったミリ秒単位の時刻フォーマット
	public static SimpleDateFormat RFC822MilliDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	// 一時間の分数
	public final static int HOUR_BY_MINUTES = 60;
	// 一分の秒数
	public final static int MINUTE_BY_SECONDS = 60;
	//　1秒のミリ秒数
	public final static int SECOND_BY_MILLI = 1000;
	// 1分のミリ秒数
	public final static int MINUTE_BY_MILLI = MINUTE_BY_SECONDS*SECOND_BY_MILLI;
	// データベースのフィールドの値を格納するためのメンバ変数
	private long mId;
	private String mTitle;
	private String mWhere;
	private GregorianCalendar mStart;
	private GregorianCalendar mEnd;
	private String mContent;

	/**
	 * 日付、時刻の文字列からDB に保存するための時刻文字列に変換する
	 * @param date　変換元の日付
	 * @param time　変換もとの時刻
	 * @return　RFC 3339形式の日時文字列
	 */
	public static String toDBDateString(String date,String time){
		// 追加可能な文字列クラスStringBuilderを作成
		StringBuilder sb = new StringBuilder();
		sb.append(date);
		sb.append("T");
		sb.append(time);
		sb.append(":00.000");
		// TimeZone文字列を作成し追加
		sb.append(timeZoneToString(TimeZone.getDefault()));
		return sb.toString();
	}

	/**
	 * タイムゾーンの文字列を生成する
	 *  RFC 3339で使用するため時、分の区切りに：を入れる
	 * @param tz タイムゾーン
	 * @return タイムゾーン文字列
	 *         例：　+9時間の場合   +09:00
	 *             -3時間の場合  -03:00
	 *             0の場合　　　　　Z
	 */
	public static String timeZoneToString(TimeZone tz){
		// カレンダークラスのインスタンスを作成
		Calendar cal = Calendar.getInstance();
		String dir=null;
		// TimeZoneからミリ秒単位のUTCからのずれを取得
		int offset = tz.getRawOffset();
		// 正負と値の分離
		if(offset<0){
			// offsetがマイナスなら符号は-
			// ずれは正にしておく
			offset = -offset;
			dir = "-";
		}else if(offset>0){
			// オフセットがプラスなら符号は＋
			dir = "+";
		}else if(offset == 0){
			// UTCに一致する場合はZを返す
			return "Z";
		}
		// 時、分を計算しCalendarにセット
		int offsetMin = offset/MINUTE_BY_MILLI;
		int offsetHour = offsetMin/HOUR_BY_MINUTES;
		offsetMin=offsetMin%60;
		cal.set(Calendar.HOUR_OF_DAY, offsetHour);
		cal.set(Calendar.MINUTE, offsetMin);
		//　正負の符号を追加した文字列を返す
		return dir+timeFormat.format(cal.getTime());
	}

	/**
	 * CalendarからDBに格納するための文字列を作成する
	 * @param cal 変換もとの値
	 * @return 日時文字列
	 */
	public static String toDBDateString(Calendar cal){
		// RFC 822形式で文字列を生成
		String dateStr = RFC822MilliDateFormat.format(cal.getTime());
		// タイムゾーン部分を処理
		if(dateStr.matches(".+[+-][0-9]{4}$")){
			dateStr = dateStr.replaceAll("([+-][0-9]{2})([0-9]{2})","$1:$2");
		}
		return dateStr;
	}
	/**
	 * 日時文字列からカレンダーへの変換
	 * @param startTime 変換もとの日時文字列
	 * @return Calendar
	 */
	public static GregorianCalendar toCalendar(String startTime) {
		GregorianCalendar calendar = new GregorianCalendar();
		// 文字列を数値以外の文字で分割して切り分ける
		String[] strs = startTime.split("[^0-9]");
		TimeZone timeZone = TimeZone.getDefault();
		if(startTime.matches("^[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]$")){
			// 日付のみの文字列　時刻を００：００に設定
			calendar.set(Calendar.YEAR, Integer.valueOf(strs[0]));
			calendar.set(Calendar.MONTH,Integer.valueOf(strs[1])-1);
			calendar.set(Calendar.DAY_OF_MONTH,Integer.valueOf(strs[2]));
			calendar.set(Calendar.HOUR_OF_DAY,0);
			calendar.set(Calendar.MINUTE,0);
			calendar.set(Calendar.SECOND,0);
			calendar.set(Calendar.MILLISECOND,0);
			calendar.setTimeZone(timeZone);
		}else{
			// 日時文字列　数値文字列を数値に変換して設定
			calendar.set(Calendar.YEAR, Integer.valueOf(strs[0]));
			calendar.set(Calendar.MONTH,Integer.valueOf(strs[1])-1);
			calendar.set(Calendar.DAY_OF_MONTH,Integer.valueOf(strs[2]));
			calendar.set(Calendar.HOUR_OF_DAY,Integer.valueOf(strs[3]));
			calendar.set(Calendar.MINUTE,Integer.valueOf(strs[4]));
			calendar.set(Calendar.SECOND,Integer.valueOf(strs[5]));
			calendar.set(Calendar.MILLISECOND,Integer.valueOf(strs[6]));
			// TimeZoneのパターンによる処理
			if(startTime.matches(".+Z$")){
				// UTC
				timeZone.setRawOffset(0);
			}else if(startTime.matches(".+\\+[0-9][0-9]:[0-9][0-9]$")){
				// オフセットがマイナス
				timeZone.setRawOffset((Integer.valueOf(strs[7])*HOUR_BY_MINUTES+Integer.valueOf(strs[8]))*MINUTE_BY_MILLI);
			}else if(startTime.matches(".+-[0-9][0-9]:[0-9][0-9]$")){
				// オフセットがプラス
				timeZone.setRawOffset(-(Integer.valueOf(strs[7])*HOUR_BY_MINUTES+Integer.valueOf(strs[8]))*MINUTE_BY_MILLI);
			}
			// TimeZoneを設定
			calendar.setTimeZone(timeZone);
		}
		return calendar;
	}

	/**
	 * toString
	 *  データベースに含まれる情報を一つの文字列として出力
	 */
	public String toString(){
		if(mStart.get(Calendar.HOUR_OF_DAY) == 0 && mStart.get(Calendar.MINUTE) == 0){
			GregorianCalendar startCal = (GregorianCalendar)mStart.clone();
			startCal.add(Calendar.DAY_OF_MONTH, 1);
			if(startCal.equals(mEnd)){
				// 開始時刻が00:00で終了日時が翌日の00:00の場合
				// 終日の予定と判断して開始日のみ表示する
				return getTitle()+"\n"
						+getStartDateString()+"\n"
						+getWhere()+"\n"
						+getContent();
			}
		}
		return getTitle()+"\n"
		+getStartDateString()+" "+getStartTimeString()+"\n"
		+getEndDateString()+" "+getEndTimeString()+"\n"
		+getWhere()+"\n"
		+getContent();
	}

	// ここから　setter/getter
	public void setId(long mId) {
		this.mId = mId;
	}

	public long getId() {
		return mId;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setWhere(String mWhere) {
		this.mWhere = mWhere;
	}

	public String getWhere() {
		return mWhere;
	}
	public void setStart(GregorianCalendar mStart) {
		this.mStart = mStart;
	}

	public void setStart(String dateString){
		this.mStart = toCalendar(dateString);
	}

	public Calendar getStart() {
		return mStart;
	}
	public String getStartString(){
		return toDBDateString(mStart);
	}
	public String getStartDateString(){
		return dateFormat.format(mStart.getTime());
	}

	public String getStartTimeString(){
		return timeFormat.format(mStart.getTime());
	}

	public void setEnd(GregorianCalendar mEnd) {
		this.mEnd = mEnd;
	}

	public String getEndString(){
		return toDBDateString(mEnd);
	}

	public void setEnd(String dateString){
		this.mEnd = toCalendar(dateString);
	}

	public Calendar getEnd() {
		return mEnd;
	}

	public String getEndDateString(){
		return dateFormat.format(mEnd.getTime());
	}

	public String getEndTimeString(){
		return timeFormat.format(mEnd.getTime());
	}

	public void setContent(String mContent) {
		this.mContent = mContent;
	}

	public String getContent() {
		return mContent;
	}
}
