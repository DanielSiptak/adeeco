package cz.cuni.mff.ms.siptak.adeecolib.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class AppMessenger {

	public static enum SERVICE {
		BUNDLE_ADD, BUNDLE_REMOVE, RUNTIME_START, RUNTIME_PAUSE
	}

	public static enum ACTIVITY {
		SERVICE_STARTED, SERVICE_STOPPED, LOG, EVENT
	}
	
	public static final String LOG_STRING = "LOG";
	
	private static volatile AppMessenger INSTANCE = null;

	Messenger mActivity = null;
	Messenger mService  = null;

	Map<String,AppLogger> mLoggers = new ConcurrentHashMap<String, AppLogger>();
	List<LogLine> mLogs = Collections.synchronizedList(new ArrayList<LogLine>(100));
	
	/**
	 *  disable instantiation
	 */
	private AppMessenger() {
	}

	public static synchronized AppMessenger getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AppMessenger();
		}
		return INSTANCE;
	}

	public Messenger getActivityMessenger() {
		return mActivity;
	}

	public void setActivityMessenger(Messenger mMessenger) {
		this.mActivity = mMessenger;
	}

	public boolean isActivityConnected() {
		return mActivity != null;
	}
	
	public void sendToActivity(Message msg) {
		if (isActivityConnected()) {
			try {
				mActivity.send(msg);
			} catch (RemoteException e) {
				// lost connection of messenger
				mActivity = null;
			}
		}
	}

	public void sendToActivity(Integer action,Bundle bundle) {
		Message msg = Message.obtain();
		msg.setData(bundle);
		msg.what = action;
		sendToActivity(msg);
	}
	
	/**
	 * @return the mMessenger
	 */
	public Messenger getServiceMessenger() {
		return mService;
	}

	/**
	 * @param mMessenger
	 *            the mMessenger to set
	 */
	public void setServiceMessenger(Messenger mMessenger) {
		this.mService = mMessenger;
	}

	public boolean isServiceConnected() {
		return mService != null;
	}
	
	public void sendToService(Message msg) {
		if (isServiceConnected()) {
			try {
				mService.send(msg);
			} catch (RemoteException e) {
				// lost connection of messenger
				mService = null;
			}
		}
	}
	
	public void sendToService(Integer action,Bundle bundle) {
		Message msg = Message.obtain();
		msg.what = action;
		msg.setData(bundle);
		sendToService(msg);
	}
	
	public AppLogger getLogger(String className) {
		synchronized(mLoggers){
			if (!mLoggers.containsKey(className)){
				mLoggers.put(className, new AppLogger(className));
			}
			return mLoggers.get(className);
		}
	}

	private void sendLog(LogLine logLine){
		mLogs.add(logLine);
		if (isActivityConnected()) {
			Bundle bundle = new Bundle();
			bundle.putSerializable(LOG_STRING, logLine);
			sendToActivity(ACTIVITY.LOG.ordinal(), bundle);
		}
	}
	
	public List<LogLine> getLogs(){
		return Collections.unmodifiableList(mLogs);
	}

	public class AppLogger{
		String mTag;
		AppMessenger mMessenger = AppMessenger.getInstance();
		
		public AppLogger(String tag) {
			this.mTag=tag;
		}
		
		public void addLog(String log){
			mMessenger.sendLog(new LogLine(mTag, log));
		}
	}
	
	/**
	 * Immutable object holding one line of log 
	 * @author Daniel Sipták
	 *
	 */
	public class LogLine implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		String mTag;
		String mLog;
		Date mDate;
		
		public LogLine(String tag,String log){
			mTag = tag;
			mLog = log;
			mDate = new Date();
		}
		
		public String getTag() {
			return mTag;
		}
		
		public String getText() {
			return mLog;
		}
		
		public Date getDate() {
			return mDate;
		}
	}
}
