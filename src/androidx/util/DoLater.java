//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.util;


public abstract class DoLater implements GUITask {
	private long millis;
	
	public DoLater(long millisToWaitBeforeDoing) {
		this.millis = millisToWaitBeforeDoing;
	}
	
	public final void after_execute() {
		this.execute();
	}

	public abstract void execute();
	
	public final void executeNonGuiTask() throws Exception {
		if (millis > 0) {
			Thread.sleep(millis);
		}
	}

	public void onFailure(Throwable t) {
	}

}
