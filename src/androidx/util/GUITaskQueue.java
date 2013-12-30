//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.util;


import android.os.Handler;
import android.os.Message;
import androidx.LogX;

public class GUITaskQueue {
  private static final int HANDLE_EXCEPTION = 0x1337;
  private static final int HANDLE_AFTER_EXECUTE = 0x1338;
  private TaskQueue taskQ;
  private Handler handler;
  private static GUITaskQueue singleton;
  
  public static GUITaskQueue getInstance() {
    if (singleton == null) {
      singleton = new GUITaskQueue();
      singleton.start();
    }
    return singleton;
  }
  
  private GUITaskQueue() {
    taskQ = new TaskQueue();
    handler = new MyHandler();
  }

  public void start() {
    taskQ.start();
  }
  
  public void stop() {
    taskQ.stop();
  }
  
  public void addTask(GUITask task) {
    taskQ.addTask(new GUITaskAdapter(task));
  }
  
  /**
   * Adds a task with an associated progress indicator.
   * The indicator's showProgressIndicator() gets called immediately
   * then the hideProgressIndicator() gets called before the GUITask's
   * handle_exception() or after_execute() method gets called.
   * 
   * @param progressIndicator
   * @param task
   */
  public void addTask(ProgressIndicator progressIndicator, GUITask task) {
  	if (progressIndicator == null) {
  		addTask(task);
  	} else {
  		addTask(new GUITaskWithProgress(task, progressIndicator));
  	}
  }
  
  private static class GUITaskWithProgress implements GUITask {
  	private GUITask delegate;
  	private ProgressIndicator progressIndicator;
  	
  	GUITaskWithProgress(GUITask _delegate, ProgressIndicator _progressIndicator) {
  		delegate = _delegate;
  		progressIndicator = _progressIndicator;
  		progressIndicator.showProgressIndicator();
  	}
  	
		public void executeNonGuiTask() throws Exception {
			delegate.executeNonGuiTask();
		}

		public void onFailure(Throwable t) {
			progressIndicator.hideProgressIndicator();
			delegate.onFailure(t);
		}

		public void after_execute() {
			progressIndicator.hideProgressIndicator();
			delegate.after_execute();
		}
  };
    
  private static class GUITaskWithSomething<T> {
    GUITask guiTask;
    T something;
    
    GUITaskWithSomething(GUITask _guiTask, T _something) {
      guiTask = _guiTask;
      something = _something;
    }
  }
  
  private void postMessage(int what, Object thingToPost) {
    Message msg = new Message();
    msg.obj = thingToPost;
    msg.what = what;
    handler.sendMessage(msg);
  }

  private void postException(GUITask task, Throwable t) {
    postMessage(HANDLE_EXCEPTION, new GUITaskWithSomething(task, t));
  }
  
  private class MyHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch(msg.what) {
        case HANDLE_EXCEPTION:
          GUITaskWithSomething<Throwable> thingie = (GUITaskWithSomething<Throwable>) msg.obj;
          thingie.guiTask.onFailure(thingie.something);
          break;
          
        case HANDLE_AFTER_EXECUTE:
          GUITask task = (GUITask) msg.obj;
          try {
            task.after_execute();
          } catch (Throwable t) {
            LogX.e(t);
          }
          break;
      }
      super.handleMessage(msg);
    }
  }
  
  private class GUITaskAdapter implements Runnable {
    private GUITask task;
    GUITaskAdapter(GUITask _task) {
      task = _task;
    }
    
    public void run() {
      try {
        task.executeNonGuiTask();
        postMessage(HANDLE_AFTER_EXECUTE, task);
      } catch (Throwable t) {
        postException(task, t);
      }
    }
  }
}
