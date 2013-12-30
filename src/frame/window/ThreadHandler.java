//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package frame.window;


import android.os.Process;

public class ThreadHandler {
    private Thread  mThread;
    private boolean mbFinished;
    private boolean mbThdIdSet;
    private int     mnThdId;

    public ThreadHandler(final Runnable r) {
        Runnable wrapper = new Runnable() {
            public void run() {
                setThdId(Process.myTid());

                try {
                    r.run();
                } finally {
                    setFinished();
                }
            }
        };

        mThread = new Thread(wrapper);
    }

    synchronized private void setThdId(int nThdId) {
        mnThdId    = nThdId;
        mbThdIdSet = true;
        ThreadHandler.this.notifyAll();
    }

    synchronized private void setFinished() {
        mbFinished = true;
    }

    synchronized public void start() {
        mThread.start();
    }

    synchronized public void setName(String name) {
        mThread.setName(name);
    }

    public void join() {
        try {
            mThread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public long getId() {
        return mThread.getId();
    }

    public Thread realThread() {
        return mThread;
    }

    synchronized public void setPriority(int androidOsPriority) {
        while (!mbThdIdSet) {
            try {
                ThreadHandler.this.wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        if (!mbFinished) {
            Process.setThreadPriority(mnThdId, androidOsPriority);
        }
    }

    synchronized public void toBackground() {
        setPriority(Process.THREAD_PRIORITY_BACKGROUND);
    }

    synchronized public void toForeground() {
        setPriority(Process.THREAD_PRIORITY_FOREGROUND);
    }
}
