/*
 * @(#)DateUtil.java 2014-1-10下午4:20:20
 * Copyright 2012 juncsoft, Inc. All rights reserved.
 */
package com.gxp.jt808.util;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间处理工具类
 * 枚举类型
 * @modificationHistory.  
 * <ul>
 * <li>liqg 2014-1-10下午4:20:20 TODO</li>
 * </ul> 
 */

public class DateUtil {
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	public static final String DD_MM_YYYY = "dd-MM-yyyy";
	public static final String MM_DD_YYYY = "MM-dd-yyyy";
	public static final String HH_MM_SS = "HH:mm:ss";
	public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	
	/**
	 * 获取当前时间
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:22:24 
	 * @return
	 */
	public static String getNowTime() {
		return getNowTime(YYYY_MM_DD_HH_MM_SS);
	}
	
	/**
	 * 获取当前时间
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:22:47 
	 * @param format 时间格式（如：yyyy-MM-dd HH:mm:ss）
	 * @return
	 */
	public static String getNowTime(String format) {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);// 可以方便地修改日期格式
		String dateString = dateFormat.format(now);
		return dateString;
	}
	
	/**
	 * 获取今天日期
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:23:18 
	 * @return 返回格式yyyy-MM-dd
	 */
	public static String getToday() {
		Calendar calendar = Calendar.getInstance();
		return new SimpleDateFormat( "yyyy-MM-dd").format(calendar.getTime());
	}
	
	/**
	 * 获得当前年份
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:25:55 
	 * @return
	 */
	public static int getYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	/**
	 * 获得当前月份
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:26:07 
	 * @return
	 */
	public static int getMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}


	 /**
	  * 判断是否闰月，用于计算当前时间加上分钟后的时间
	  * @author liqg
	  * @creationDate. 2014-1-10 下午4:26:41 
	  * @param year	年份
	  * @return
	  */
    public static boolean isLeapYear(int year) {
        // 能被100整除, 不能被400整除的年份, 不是闰年.
        // 能被100整除, 也能被400整除的年份, 是闰年.
		if ((year % 100) == 0) {
			return ((year % 400) == 0);
		} else {// 不能被100整除, 能被4整除的年份是闰年.
			return ((year % 4) == 0);
		}
    }

	/**
	 * 获得今天在本年的第几天
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:27:38 
	 * @return
	 */
	public static int getDayOfYear() {
		return Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
	}
	
	/**
	 * 获得今天在本月的第几天(获得当前日)
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:27:46 
	 * @return
	 */
	public static int getDayOfMonth() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获得今天在本周的第几天
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:29:31 
	 * @return 星期天1，星期一2 ......星期六7
	 */
	public static int getDayOfWeek() {
		return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 获得今天是这个月的第几周
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:29:39 
	 * @return
	 */
	public  static int getWeekOfMonth() {
		return Calendar.getInstance().get(Calendar.DAY_OF_WEEK_IN_MONTH);
	}

	/**
	 * 获取当月第一天
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:30:43 
	 * @param format 时间格式（如：yyyy-MM-dd）
	 * @return
	 */
	public static String getFirstDayOfMonth(String format) {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当月的1号
		str = sdf.format(lastDate.getTime());
		return str;
	}
	
	/**
	 * 获取当月最后一天
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:31:05 
	 * @param format 时间格式（如：yyyy-MM-dd）
	 * @return
	 */
	public static String getLastDayOfMonth(String format) {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号
		lastDate.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天

		str = sdf.format(lastDate.getTime());
		return str;
	}

	/**
	 * 计算上月第一天
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:31:56 
	 * @param format 时间格式（如：yyyy-MM-dd）
	 * @return
	 */
	public static String getFirstDayOfPreviousMonth(String format) {
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, -1);// 减一个月，变为上月的1号
		String date =new SimpleDateFormat(format).format(lastDate.getTime());
		return date;
	}
	
	/**
	 * 计算上月最后一天
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:32:11 
	 * @param format 时间格式（如：yyyy-MM-dd）
	 * @return
	 */
	public static String getLastDayOfPreviousMonth(String format) {
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.DATE,-1);//减去一天，变为上月最后一天
		String date =new SimpleDateFormat(format).format(lastDate.getTime());
		return date;
	}
	
	/**
	 * 计算下月第一天
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:32:22 
	 * @param format 时间格式（如：yyyy-MM-dd）
	 * @return
	 */
	public static String getFirstDayOfNextMonth(String format) {
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号
		return new SimpleDateFormat(format).format(lastDate.getTime());
	}
	
	/**
	 * 计算下月最后一天
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:32:34 
	 * @param format 时间格式（如：yyyy-MM-dd）
	 * @return
	 */
	public static String getLastDayOfNextMonth(String format) {
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, 2); //加2个月，变为下下月的1号
		lastDate.add(Calendar.DATE,-1);//减去一天，变为下月最后一天
		return new SimpleDateFormat(format).format(lastDate.getTime());
	}
	
	/**
	 * 某月第一天
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:32:47 
	 * @param num -1是上个月，1是下个月
	 * @param format 时间格式（如：yyyy-MM-dd）
	 * @return
	 */
	public static String getMonthFirst(int num, String format) {
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, num);// 加num个月，变为某月的1号
		String date =new SimpleDateFormat(format).format(lastDate.getTime());
		return date;
	}
	/**
	 * 某月最后一天
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:38:11 
	 * @param num -1是上个月，1是下个月
	 * @param format 时间格式（如：yyyy-MM-dd）
	 * @return
	 */
	public static String getMonthEnd(int num, String format) {
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, num+1);// 加num个月，变为某某月的1号
		lastDate.add(Calendar.DATE,-1);//减去一天，变为某月最后一天
		String date = new SimpleDateFormat(format).format(lastDate.getTime());
		return date;
	}

	/**
	 * 获得本周一的日期
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:38:29 
	 * @return yyyy-MM-dd
	 */
	public static String getMondayOFWeek() {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.add(Calendar.WEEK_OF_MONTH, 0);
		return DateFormat.getDateInstance().format(calendar.getTime());
	}

	/**
	 * 获得本周星期日的日期
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:39:04 
	 * @return yyyy-MM-dd
	 */
	public static String getSundayOFWeek() {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		calendar.add(Calendar.WEEK_OF_MONTH, 0);
		return DateFormat.getDateInstance().format(calendar.getTime());
	}
	
	/**
	 * 获取上周一日期
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:39:17 
	 * @return yyyy-MM-dd
	 */
	public static String getPreviousMonday() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1*7);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return DateFormat.getDateInstance().format(calendar.getTime());
	}
	/**
	 * 获取上周日日期
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:39:29 
	 * @return yyyy-MM-dd
	 */
	public static String getPreviousSunday() {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		calendar.add(Calendar.WEEK_OF_MONTH, -1);
		return DateFormat.getDateInstance().format(calendar.getTime());
	}
	
	/**
	 * 获取下周一日期
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:39:39 
	 * @return yyyy-MM-dd
	 */
	public static String getNextMonday() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1*7);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return DateFormat.getDateInstance().format(calendar.getTime());
	}
	
	/**
	 * 获取下周日日期
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:39:51 
	 * @return yyyy-MM-dd
	 */
	public static String getNextSunday() {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		calendar.add(Calendar.WEEK_OF_MONTH, 1);
		return DateFormat.getDateInstance().format(calendar.getTime());
	}
	
	/**
	 * 获取相对当前周某周周一日期
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:40:05 
	 * @param num	-1 上一周   0 本周  1 下一周
	 * @param format 时间格式（如：yyyy-MM-dd）
	 * @return
	 */
	public static String getWeekMonday(int num, String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, num*7);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return new SimpleDateFormat(format).format(calendar.getTime());
	}
	
	/**
	 * 获取相对当前周某周周日日期
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:41:21 
	 * @param num	-1 上一周   0 本周  1 下一周
	 * @param format 时间格式（如：yyyy-MM-dd）
	 * @return
	 */
	public static String getWeekSunday(int num, String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		calendar.add(Calendar.WEEK_OF_MONTH, num);
		return new SimpleDateFormat(format).format(calendar.getTime());
	}
	
	/**
	 * 根据一个日期，返回是星期几的字符串
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:41:49 
	 * @param strDate 日期（格式：yyyy-MM-dd）
	 * @return 格式如：星期四
	 */
	public static String getWeek(String strDate) {
		// 再转换为时间
		Date date = new SimpleDateFormat(YYYY_MM_DD).parse(strDate, new ParsePosition(0));;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		// int hour=c.get(Calendar.DAY_OF_WEEK);
		// hour中存的就是星期几了，其范围 1~7
		// 1=星期日 7=星期六，其他类推
		return new SimpleDateFormat("EEEE").format(c.getTime());
	}
	/**
	 * 获得一个日期所在周的星期几的日期
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:48:55 
	 * @param strDate 日期
	 * @param num	取值范围 0~6，0=星期日、1=星期一、2=星期二，依此类推
	 * @param format "yyyy-MM-dd"
	 * @return
	 */
	public static String getWeek(String strDate, String num, String format){
		Date date = getDateParse(strDate, YYYY_MM_DD);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if("1".equals(num)){
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		}else if("2".equals(num)){
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
		}else if("3".equals(num)){
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		}else if("4".equals(num)){
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		}else if("5".equals(num)){
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		}else if("6".equals(num)){
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		}else if("0".equals(num)){
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		}
		return new SimpleDateFormat(format).format(calendar.getTime());
	}
	
	/**
	 * 获得相隔几天后的的时间
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:50:26 
	 * @param num	相隔天数
	 * @param format 时间格式（yyyy-MM-dd HH:mm:ss）
	 * @return
	 */
	public static String addDays(int num, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, num);
		String str = sdf.format(calendar.getTime());
		return str;
	}
	/**
	 * 获得相隔几小时后的时间
	 * @author liqg
	 * @creationDate. 2015-3-11 下午5:12:39 
	 * @param num
	 * @param format
	 * @return
	 */
	public static String addHour(int num, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, num);
		
		return sdf.format(calendar.getTime());
	}
	/**
	 * 取得相对于当前时间增加天数/月数/年数后的日期.
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:52:35 
	 * @param field	段,如"year","month","day","hour","minute","second"对大小写不敏感
	 * @param num 增量(减少用负数表示),如:5,-1
	 * @return 格式化后的字符串 如"2000-02-01 21:10:12"
	 */
	public static String addDate(String field, int num) {
		// 当前日期和前一天
		SimpleDateFormat dateformat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
		Calendar rightNow = Calendar.getInstance();
		java.util.Date dt = new java.util.Date();
		rightNow.setTime(dt);

		String tmpField = field.toUpperCase().trim();

		int intField = Calendar.DATE;
		if ("YEAR".equals(tmpField)) {
			intField = Calendar.YEAR;
		} else if ("MONTH".equals(tmpField)) {
			intField = Calendar.MONTH;
		} else if ("DAY".equals(tmpField)) {
			intField = Calendar.DAY_OF_MONTH;
		} else if ("HOUR".equals(tmpField)) {
			intField = Calendar.HOUR_OF_DAY;
		} else if ("MINUTE".equals(tmpField)) {
			intField = Calendar.MINUTE;
		} else if ("SECOND".equals(tmpField)) {
			intField = Calendar.SECOND;
		}
		
		rightNow.add(intField, num);
		String day = dateformat.format(rightNow.getTime());
		return day;
	}
	
	/**
	 * 将日期转换成字符串
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:56:11 
	 * @param dateTime 日期
	 * @param format 转换格式
	 * @return
	 */
	public static String convertDateToString(Date dateTime, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(dateTime);
	}
	
	public static String timestampToDateString(Long timestamp, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(new Date(timestamp));
	}
	
	/**
	 * 字符串转时间格式
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:56:37 
	 * @param strDate 时间字符串
	 * @param format 转换格式
	 * @return
	 */
	public static Date  getDateParse(String strDate, String format) {
		return new SimpleDateFormat(format).parse(strDate, new ParsePosition(0));
	}
	

	/**
	 * 两个时间相差的天数
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:57:07 
	 * @param startDate 起始时间
	 * @param endDate 结束时间
	 * @return
	 */
	public static long getDiffDate(String startDate, String endDate) {
		if (startDate == null || startDate.equals("")){
			return 0;
		}
		if (endDate == null || endDate.equals("")) {
			return 0;
		}
		// 转换为标准时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date dateBegin = null;
		java.util.Date dateEnd = null;
		try {
			dateBegin = df.parse(startDate);
			dateEnd = df.parse(endDate);
		} catch (Exception e) {
			return 0;
		}
		long diffDay = (dateBegin.getTime() - dateEnd.getTime()) / (24 * 60 * 60 * 1000);
		return diffDay;
	}

	/**
	 * 两个字符时间相隔的天数
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:57:43 
	 * @param startDate 起始时间
	 * @param endDate 结束时间
	 * @return
	 */
	public static long getSpanDays(String startDate, String endDate) {
		long spanDay = Math.abs(getDiffDate(startDate, endDate));
		return spanDay;
	}
	
	/**
	 * 比较两个时间的大小 strDate1 > strDate2  返回true
	 * @author liqg
	 * @creationDate. 2014-1-10 下午4:58:02 
	 * @param strDate1
	 * @param strDate2
	 * @param format 时间格式
	 * @return
	 */
	public static boolean getTimeStepSize(String strDate1, String strDate2, String format){
		Date date1 = getDateParse(strDate1, format);
		Date date2 = getDateParse(strDate2, format);
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		return time1 > time2 ? true: false;
	}
	/**
	 * 传入指定日期获取下一天
	 * @author chengha
	 * @param everyDay
	 * @return
	 */
	public static String getAfterDay(String everyDay){
		Calendar c=Calendar.getInstance();
		Date date=getDateParse(everyDay, "yyyy-MM-dd");
		c.setTime(date);
		int day=c.get(Calendar.DATE);
		c.set(Calendar.DATE, day+1);
		String dayAfter=convertDateToString(c.getTime(), "yyyy-MM-dd");
		return dayAfter;
		
	}
	/**
	 * 测试
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		//DateTimeUtil tt = new DateTimeUtil();
		
		
	}

}
