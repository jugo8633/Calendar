//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata.xml;


import java.io.PrintWriter;
import androidx.gdata.XMLSerializer;
import androidx.gdata.Where;

/**
 * For serialize {@link=Where} type.
 * 
 * @author Luke Liu
 *
 */
public class WhereSerializer implements XMLSerializer<Where> {
	private String extraIndent = "";

	public void setExtraIndent(String extraIndent) {
		this.extraIndent = extraIndent;
	}

	public void serialize(PrintWriter out, Where thing) {
		out.println(extraIndent + "  <gd:where valueString='" + thing.getValueString() + "'></gd:where>");
	}

}
