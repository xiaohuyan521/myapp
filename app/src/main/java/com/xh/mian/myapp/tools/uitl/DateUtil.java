package com.xh.mian.myapp.tools.uitl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
	public static final String FORMAT_DATETIME="yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_DATETIME_SHORT="yyyy-MM-dd HH:mm";
	public static final String FORMAT_DATE="yyyy-MM-dd";
	public static final String FORMAT_SURGERY_TIME="yyyy年M月d日H时";
	public static final String FORMAT_ORDER_LONG="yyyyMMddHH";
	public static final String FORMAT_ORDER="yyyyMMdd";
	public static final String FORMAT_YEAR="yyyy";
	public static final String FORMAT_MONTH="M";
	public static final String FORMAT_YEAR_MONTH="yyyy年M月";
	public static final String FORMAT_HOUR_MINUTE="HH：mm";
	public static final String FORMAT_DATE_YEAR_MONTH="yyyy-MM";
	public static final String FORMAT_FULL="yyyyMMddHHmmssSSS";
	public static Date add(int field, int amount) {
		return add(new Date(), field, amount);
	}

	public static Date add(Date atime, int field, int amount) {
		GregorianCalendar weekCl = new GregorianCalendar();
		weekCl.setTime(atime);
		weekCl.add(field, amount);
		return weekCl.getTime();
	}

	
	public static String formatDate(Date atime, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(atime);
	}


	public static Date parseDate(String str, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		try {
			return formatter.parse(str);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	public static String dateAddHour(String srcDateStr,int amount){
		if("".equals(srcDateStr))
			return "";
		Date srcDate=parseDate(srcDateStr,FORMAT_DATETIME);
		if(null==srcDate)
			return null;
		Date newDate=add(srcDate,Calendar.HOUR_OF_DAY,amount);
		return formatDate(newDate,FORMAT_DATETIME);
	}
	
	public static String dateAddMonth(String srcDateStr,int amount){
		if("".equals(srcDateStr))
			return "";
		Date srcDate=parseDate(srcDateStr,FORMAT_DATE);
		if(null==srcDate)
			return null;
		Date newDate=add(srcDate,Calendar.MONTH,amount);
		return formatDate(newDate,FORMAT_DATE);
	}
	
	
	public static String dateAddDay(String srcDateStr,int amount){
		if("".equals(srcDateStr))
			return "";
		Date srcDate=parseDate(srcDateStr,FORMAT_DATE);
		if(null==srcDate)
			return null;
		Date newDate=add(srcDate,Calendar.DAY_OF_YEAR,amount);
		return formatDate(newDate,FORMAT_DATE);
	}
	/**
	 * 获得几天之前或者几天之后的日期
	 * @param diff 差值：正的往后推，负的往前推
	 * @return
	 */
	public static String getOtherDay(int diff) {
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.add(Calendar.DATE, diff);
		return getDateFormat(mCalendar.getTime());
	}
	/**
	 * 将date转成yyyy-MM-dd字符串<br>
	 * @param date Date对象
	 * @return yyyy-MM-dd
	 */
	public static String getDateFormat(Date date) {
		return dateSimpleFormat(date, defaultDateFormat.get());
	}
	/**
	 * 将date转成字符串
	 * @param date Date
	 * @param format SimpleDateFormat
	 * <br>
	 * 注： SimpleDateFormat为空时，采用默认的yyyy-MM-dd HH:mm:ss格式
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String dateSimpleFormat(Date date, SimpleDateFormat format) {
		if (format == null)
			format = defaultDateTimeFormat.get();
		return (date == null ? "" : format.format(date));
	}
	/** yyyy-MM-dd HH:mm:ss格式 */
	public static final ThreadLocal<SimpleDateFormat> defaultDateTimeFormat = new ThreadLocal<SimpleDateFormat>() {

		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(FORMAT_DATETIME);
		}
	};
	/** yyyy-MM-dd格式 */
	public static final ThreadLocal<SimpleDateFormat> defaultDateFormat = new ThreadLocal<SimpleDateFormat>() {

		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(FORMAT_DATE);
		}

	};


}