//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.util;


import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Date;

import com.hp.ij.common.service.gdata.client.calendar.widget.LogX;

/**
 * Utilities for Date, Time, TimeZone
 * 
 * @author Luke Liu
 *
 */
public class TimeZoneUtil {
	
	private static TimeZone mTimeZone = TimeZone.getDefault();
	/**
	 * Convert the original SHORT TimeZone format from GMT[+, -]hh:mm to [+, -]hh:mm
	 *  
	 * @return String in [+, -]hh:mm 
	 */
	public static String getTimeZoneOffset() {
		StringBuffer stringBufferGMT = new StringBuffer();
		TimeZone timeZone = TimeZone.getDefault();
		int intRawOffSet = timeZone.getRawOffset();
		int intOneHour = 60 * 60 * 1000;
		int intOffSet = intRawOffSet / intOneHour; 
		
		if(timeZone.inDaylightTime(new Date()))
			++intOffSet;
		
		if(intOffSet >= 0)	stringBufferGMT.append("+");
		else	stringBufferGMT.append("-");
		
		if(intOffSet >= 10)	stringBufferGMT.append(Integer.toString(intOffSet) + ":00");
		else if((intOffSet < 10) && (intOffSet >= 0))	stringBufferGMT.append("0" + Integer.toString(intOffSet) + ":00");
		else if((intOffSet < 0) && (intOffSet > -10))	stringBufferGMT.append("0" + Integer.toString(Math.abs(intOffSet)) + ":00");
		else stringBufferGMT.append(Integer.toString(Math.abs(intOffSet)) + ":00");
		
		return stringBufferGMT.toString();
	}

	/**
	 * Generate hh:mm - hh:mm [AP, PM] format
	 * 
	 * @param strDateStart time start
	 * @param strDateEnd time end
	 * @return string represent hh:mm - hh:mm [AP, PM] format
	 */
	public static String getTimeAMPM(String strDateStart, String strDateEnd) {
		SimpleDateFormat simpleDateFormat;
		if(strDateStart.indexOf("T") == -1)
			simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		else
			simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			
		Date dateStart = simpleDateFormat.parse(strDateStart, new ParsePosition(0));
		Date dateEnd = simpleDateFormat.parse(strDateEnd, new ParsePosition(0));
		StringBuffer stringBufferReturn = new StringBuffer();
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(dateStart);
		stringBufferReturn.append(gregorianCalendar.get(Calendar.HOUR)); 
		if(gregorianCalendar.get(Calendar.MINUTE) != 0)	stringBufferReturn.append(":" + gregorianCalendar.get(Calendar.MINUTE));
		stringBufferReturn.append(" - ");

		gregorianCalendar.setTime(dateEnd);
		stringBufferReturn.append(gregorianCalendar.get(Calendar.HOUR));
		if(gregorianCalendar.get(Calendar.MINUTE) != 0)	stringBufferReturn.append(":" + gregorianCalendar.get(Calendar.MINUTE));
		if(gregorianCalendar.get(Calendar.AM_PM) == 0)	stringBufferReturn.append(" AM");
		else stringBufferReturn.append(" PM");

		return stringBufferReturn.toString();
	}

	/**
	 * Return TimeZone ID
	 *  
	 * @return String in [+, -]hh:mm 
	 */
	public static String getTimeZoneDisplayName() {
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = timeZone.getID();
		return strTimeZone;
	}

	/**
	 * Generate hh:mm [AP, PM]-hh:mm [AP, PM] format
	 * 
	 * @param strDateStart time start
	 * @param strDateEnd time end
	 * @return string represent hh:mm [AP, PM]-hh:mm [AP, PM] format
	 */
	public static String getStartAndEndTimeAMPM(String strDateStart, String strDateEnd) {
		SimpleDateFormat simpleDateFormat;
		if(strDateStart.indexOf("T") == -1)
			simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		else
			simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			
		Date dateStart = simpleDateFormat.parse(strDateStart, new ParsePosition(0));
		Date dateEnd = simpleDateFormat.parse(strDateEnd, new ParsePosition(0));
		StringBuffer stringBufferReturn = new StringBuffer();
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(dateStart);
		stringBufferReturn.append(gregorianCalendar.get(Calendar.HOUR)); 
		if(gregorianCalendar.get(Calendar.MINUTE) != 0)	stringBufferReturn.append(":" + gregorianCalendar.get(Calendar.MINUTE));
		if(gregorianCalendar.get(Calendar.AM_PM) == 0)	stringBufferReturn.append(" AM");
		else stringBufferReturn.append(" PM");
		stringBufferReturn.append("-");

		gregorianCalendar.setTime(dateEnd);
		stringBufferReturn.append(gregorianCalendar.get(Calendar.HOUR));
		if(gregorianCalendar.get(Calendar.MINUTE) != 0)	stringBufferReturn.append(":" + gregorianCalendar.get(Calendar.MINUTE));
		if(gregorianCalendar.get(Calendar.AM_PM) == 0)	stringBufferReturn.append(" AM");
		else stringBufferReturn.append(" PM");

		return stringBufferReturn.toString();
	}

	/**
	 * Convert DateTime between different TimeZone
	 * @param strDateTime
	 * @return
	 */
	public static String getTimeByTimeZone(String strDateTime) {
		//LogX.d("original: " + strDateTime);
		String strTimeZoneSource = strDateTime.substring(strDateTime.indexOf("000") + "000".length());
		TimeZone timeZoneSource = TimeZone.getTimeZone("GMT" + strTimeZoneSource);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		if(strDateTime.indexOf("T") == -1)
			strDateTime = strDateTime + "T00:00:00.000"; 
		else
			simpleDateFormat.setTimeZone(timeZoneSource);
		
		Date date = simpleDateFormat.parse(strDateTime, new ParsePosition(0));
		
		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		TimeZone timeZoneTarget = TimeZone.getTimeZone("GMT" + getTimeZoneOffset());
		simpleDateFormat.setTimeZone(timeZoneTarget);
		String strTimeTarget = simpleDateFormat.format(date);
		//LogX.d("target: " + strTimeTarget + ".000" + getTimeZoneOffset());
		return strTimeTarget + ".000" + getTimeZoneOffset();
	}


}
