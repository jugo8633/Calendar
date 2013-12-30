//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata;


import java.io.Serializable;
import androidx.util.TimeZoneUtil;

/**
 * A gd:recurrence element contains syntax for recurring events based upon the iCalendar standard(RFC 2445)
 * More detail info refer to http://www.ietf.org/rfc/rfc2445.txt page 47 for time section
 * 
 * @author Luke Liu
 */
public class Recurrence implements Serializable {
	
	/**
	 * Flag indicate has recurrences or not;
	 */
	private boolean hasRecurrence;
	
	/**
	 * DTSTART
	 */
	private String dtStart;
	
	/**
	 * DTEND
	 */
	private String dtEnd;
	
	/**
	 * FREQ
	 */
	private String freq;

	/**
	 * BYDAY 4.3.10 Recurrence Rule
	 * Formal Definition: The value type is defined by the following notation:
     * recur    = 	   "FREQ"=freq *(
     * 
     *                 ( ";" "UNTIL" "=" enddate ) /
     *                 ( ";" "COUNT" "=" 1*DIGIT ) /
     * 
     *                 ( ";" "INTERVAL" "=" 1*DIGIT )          /
     *                 ( ";" "BYSECOND" "=" byseclist )        /
     *                 ( ";" "BYMINUTE" "=" byminlist )        /
     *                 ( ";" "BYHOUR" "=" byhrlist )           /
     *                 ( ";" "BYDAY" "=" bywdaylist )          /
     *                 ( ";" "BYMONTHDAY" "=" bymodaylist )    /
     *                 ( ";" "BYYEARDAY" "=" byyrdaylist )     /
     *                 ( ";" "BYWEEKNO" "=" bywknolist )       /
     *                 ( ";" "BYMONTH" "=" bymolist )          /
     *                 ( ";" "BYSETPOS" "=" bysplist )         /
     *                 ( ";" "WKST" "=" weekday )              /
     *                 freq       = "SECONDLY" / "MINUTELY" / "HOURLY" / "DAILY" / "WEEKLY" / "MONTHLY" / "YEARLY"
     *                 weekday    = "SU" / "MO" / "TU" / "WE" / "TH" / "FR" / "SA"
     *                 byday      = weekday

	 */
	private String byDay;
	
	/**
	 * UNTIL
	 */
	private String until;
	
	/**
	 * Timezone
	 */
	private String timeZone;
	
	public Recurrence(boolean hasRecurrence, String dtStart, String dtEnd, String freq, String byDay, String until) {
		if(hasRecurrence) {
			this.dtStart = dtStart;
			this.dtEnd = dtEnd;
			this.freq = freq;
			this.byDay = byDay;
			this.until = until;
			this.setTimeZone(TimeZoneUtil.getTimeZoneDisplayName());
		}
		
		this.hasRecurrence = hasRecurrence;
	}

	
	public Recurrence(boolean hasRecurrence) {
		this.hasRecurrence = hasRecurrence;
	}

	public void setDtStart(String dtStart) {
		this.dtStart = dtStart;
	}

	public String getDtStart() {
		return dtStart;
	}

	public void setDtEnd(String dtEnd) {
		this.dtEnd = dtEnd;
	}

	public String getDtEnd() {
		return dtEnd;
	}

	public void setFreq(String freq) {
		this.freq = freq;
	}

	public String getFreq() {
		return freq;
	}

	public void setByDay(String byDay) {
		this.byDay = byDay;
	}

	public String getByDay() {
		return byDay;
	}

	public void setUntil(String until) {
		this.until = until;
	}

	public String getUntil() {
		return until;
	}

	public void setHasRecurrence(boolean hasRecurrence) {
		this.hasRecurrence = hasRecurrence;
	}

	public boolean isHasRecurrence() {
		return hasRecurrence;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getTimeZone() {
		return timeZone;
	}

}
