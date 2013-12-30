//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegEx {

	/**
	 * A regular expression to extract id from url
	 *  
	 * @param strPattern, pattern string "(?:full/)(.+)"
	 * @param strFind, to be find string
	 * @return match the pattern
	 */
	public static String exetractId(String strPattern, String strFind) {
		//Pattern pattern = Pattern.compile("(?:full/)(.+)");
		Pattern pattern = Pattern.compile(strPattern);
		Matcher matcher = pattern.matcher(strFind);
		matcher.find();
		return matcher.group(1);
	}

	/**
	 * Extract Date from "yyyy-MM" to yyyy and MM
	 * 
	 * @param strFind, format "yyyy-MM"
	 * @return yyyy and MM
	 */
	public static String[] extractDate(String strFind) {
		Pattern pattern = Pattern.compile("([0-9]{4})-([0-9]{2})");
		Matcher matcher = pattern.matcher(strFind);
		String[] strReturns = new String[matcher.groupCount()];
		if(matcher.find()) {
			for(int i=0; i<matcher.groupCount(); i++)	strReturns[i] = matcher.group(i);
		}
		
		return strReturns;
	}
}
