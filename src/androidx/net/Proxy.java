//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.net;


import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.SystemProperties;
import android.provider.Settings;
import com.hp.ij.common.service.gdata.client.calendar.widget.LogX;

final public class Proxy {
	static final public String PROXY_CHANGE_ACTION = "android.intent.action.PROXY_CHANGE";

	/**
	 * Return the proxy host set by the user.
	 * 
	 * @param ctx
	 *            A Context used to get the settings for the proxy host.
	 * @return String containing the host name. If the user did not set a host
	 *         name it returns the default host. A null value means that no host
	 *         is to be used.
	 */
	static final public String getHost(Context ctx) {
		ContentResolver contentResolver = ctx.getContentResolver();
		String host = Settings.Secure.getString(contentResolver,
				Settings.Secure.HTTP_PROXY);
		if (host != null) {
			int i = host.indexOf(':');
			if (i == -1) {
				return null;
			}
			return host.substring(0, i);
		}
		return getDefaultHost();
	}

	/**
	 * Return the proxy port set by the user.
	 * 
	 * @param ctx
	 *            A Context used to get the settings for the proxy port.
	 * @return The port number to use or -1 if no proxy is to be used.
	 */
	static final public int getPort(Context ctx) {
		ContentResolver contentResolver = ctx.getContentResolver();
		String host = Settings.Secure.getString(contentResolver,
				Settings.Secure.HTTP_PROXY);
		if (host != null) {
			int i = host.indexOf(':');
			if (i == -1) {
				return -1;
			}
			return Integer.parseInt(host.substring(i + 1));
		}
		return getDefaultPort();
	}

	/**
	 * Return the default proxy host specified by the carrier.
	 * 
	 * @return String containing the host name or null if there is no proxy for
	 *         this carrier.
	 */
	static final public String getDefaultHost() {
		String host = SystemProperties.get("net.gprs.http-proxy");
		if (host != null) {
			Uri u = Uri.parse(host);
			host = u.getHost();
			return host;
		} else {
			return null;
		}
	}

	/**
	 * Return the default proxy port specified by the carrier.
	 * 
	 * @return The port number to be used with the proxy host or -1 if there is
	 *         no proxy for this carrier.
	 */
	static final public int getDefaultPort() {
		String host = SystemProperties.get("net.gprs.http-proxy");
		if (host != null) {
			Uri u = Uri.parse(host);
			return u.getPort();
		} else {
			return -1;
		}
	}

}
