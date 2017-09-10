/**
 * 
 */
package me.libme.fn.netty.util;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author J
 */
public abstract class JDateUtils extends DateUtils{
	
	private static final Logger LOGGER= LoggerFactory.getLogger(JDateUtils.class);

	public static final String ddMMyyyyHHmmss="dd/MM/yyyy HH:mm:ss";
	
	public static final String yyyyMMddHHmmss="yyyy-MM-dd HH:mm:ss";
	
	public static final String yyyyMMddHHmmssSSS="yyyy-MM-dd HH:mm:ss,SSS";
	
	
	public static final String ANY="ANY";
	
	
	/**
	 * format {@link Date} in the form of "yyyy-MM-dd".
	 * @param date
	 * @return 
	 */
	public static String format(Date date){
		return new SimpleDateFormat("yyyy-MM-dd").format(date) ;
	}
	
	/**
	 * format {@link Date} with the argument 'pattern'.
	 * @param date
	 * @param pattern
	 * @return 
	 */
	public static String format(Date date,String pattern){
		return new SimpleDateFormat(pattern).format(date) ;
	}
	

	/**
	 *  format {@link Date} in the form of " yyyy-MM-dd HH:mm:ss".
	 * @param timestamp
	 * @return 
	 */
	public static String formatWithSeconds(Date timestamp){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp) ;
	}
	
	public static String formatWithMSeconds(Date timestamp){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS").format(timestamp) ;
	}
	
	/**
	 * 返回距离当前日期的时间差。
	 * 比如： 5分钟之前，1小时之前，2年之前等等
	 * @param timestamp
	 * @return
	 */
	public static String getTimeOffset(Date timestamp){
		String desc="";
		int minute=60000;
		int hour=minute*60;
		int day=minute*60*24;
		long month=day*30;
		long year=month*12;
		long timeOffset=Calendar.getInstance().getTime().getTime()- timestamp.getTime();
		long div=-1;
		if((div=timeOffset/year)>0){
			desc=div+"年之前";
		}
		else if((div=timeOffset/month)>0){
			desc=div+"月之前";
		}
		else if((div=timeOffset/day)>0){
			desc=div+"天之前";
		}
		else if((div=timeOffset/hour)>0){
			desc=div+"小时之前";
		}
		else if((div=timeOffset/minute)>0){
			desc=div+"分钟之前";
		}
		return desc;
	}
	
	/**
	 * get time description .
	 * <p> (1year12moth30day24hour60minute60second1000millisecond)
	 * @param millisecond  millisecond
	 * @return
	 */
	public static String getTimeOffset(long millisecond){
		String desc="";
		int msecond=1;
		int second=1000*msecond;
		int minute=second*60;
		int hour=minute*60;
		int day=minute*60*24;
		long month=day*30;
		long year=month*12;
		long timeOffset=millisecond;
		long div=-1;
		if((div=timeOffset/year)>0){
			desc=desc+div+"year";
			timeOffset=timeOffset-div*year;
		}
		if((div=timeOffset/month)>0){
			desc=desc+div+"month";
			timeOffset=timeOffset-div*month;
		}
		if((div=timeOffset/day)>0){
			desc=desc+div+"day";
			timeOffset=timeOffset-div*day;
		}
		if((div=timeOffset/hour)>0){
			desc=desc+div+"hour";
			timeOffset=timeOffset-div*hour;
		}
		if((div=timeOffset/minute)>0){
			desc=desc+div+"minute";
			timeOffset=timeOffset-div*minute;
		}
		if((div=timeOffset/minute)>0){
			desc=desc+div+"minute";
			timeOffset=timeOffset-div*minute;
		}
		if((div=timeOffset/second)>0){
			desc=desc+div+"second";
			timeOffset=timeOffset-div*second;
		}
		if(timeOffset>0){
			desc=desc+timeOffset+"millisecond";
		}
		return desc;
	}
	
	/**
	 * parse the date of the form of "yyyy-MM-dd"
	 * @param date
	 * @return {@link Date}
	 * @throws Exception 
	 */
	public static Date parseDate(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * parse the date of the form "yyyy-MM-dd"
	 * @param date
	 * @return {@link Timestamp}
	 * @throws Exception 
	 */
	public static Timestamp parseTimestamp(String date) {
		try {
			return new Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * parse the date of the form "yyyy-MM-dd HH:mm:ss"
	 * @param date
	 * @return {@link Timestamp}
	 * @throws Exception 
	 */
	public static Timestamp parseTimestampWithSeconds(String date) {
		return parseTimestampWithSeconds(date, "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 
	 * @param date
	 * @param timeformat pass string format
	 * @return
	 */
	public static Timestamp parseTimestampWithSeconds(String date,String timeformat) {
		try {
			return new Timestamp(new SimpleDateFormat(timeformat).parse(date).getTime());
		} catch (ParseException e) {
			LOGGER.error("date format : "+timeformat+" date : "+date);
			throw new RuntimeException(e);
		}
	}
	
}
