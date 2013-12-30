//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata.xml;

import org.w3c.dom.Node;
import androidx.xml.XMLBuilder;
import androidx.gdata.AccessLevel;

/**
 * A AccessLevel Deserializer
 * 
 * @author Luke Liu
 *
 */
public class AccessLevelDeserializer extends XMLBuilder<AccessLevel> {

	@Override
	public AccessLevel build(Node nodeAccessLevel) {
		this.node = nodeAccessLevel;
		String valueString = getAttribute("value");
		return new AccessLevel(valueString);
	}

}
