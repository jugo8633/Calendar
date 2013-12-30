//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata;


import java.io.Serializable;

public class Where implements Serializable{
	
	private String valueString;

	public Where(String valueString) {
		this.valueString = valueString;
	}
	
	public String getValueString() {
		return valueString;
	}

}