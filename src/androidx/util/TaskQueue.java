//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.util;


import android.util.Log;
import java.util.LinkedList;

public class TaskQueue {
  private LinkedList<Runnable> tasks;
  private Thread thread;
  private boolean running;
  private Runnable internalRunnable;
  
  private class InternalRunnable implements Runnable {
    public void run() {
      internalRun();
    }
  }
  
  public TaskQueue() {
    tasks = new LinkedList<Runnable>();
    internalRunnable = new InternalRunnable();
  }
  
  public void start() {
    if (!running) {
      thread = new Thread(internalRunnable);
      thread.setDaemon(true);
      running = true;
      thread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
      thread.start();
    }
  }
  
  public void stop() {
    running = false;
  }

  public void addTask(Runnable task) {
    synchronized(tasks) {
        tasks.addFirst(task);
        tasks.notify(); // notify any waiting threads
    }
  }
  
  private Runnable getNextTask() {
    synchronized(tasks) {
      if (tasks.isEmpty()) {
        try {
          tasks.wait();
        } catch (InterruptedException e) {
//          Log.e("androidx", "Task interrupted", e);
          stop();
        }
      }
      return tasks.removeLast();
    }
  }
  
  
  private void internalRun() {
    while(running) {
      Runnable task = getNextTask();
      try {
        task.run();
      } catch (Throwable t) {
 //       Log.e("androidx", "Task threw an exception", t);
      }
    }
  }

}
