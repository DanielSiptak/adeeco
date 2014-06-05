package cz.cuni.mff.ms.siptak.adeecolib.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cz.cuni.mff.ms.siptak.adeecolib.service.AppMessenger.AppLogger;

public class AppMessenger {


	private static volatile AppMessenger INSTANCE = null;
	
	Map<String,AppLogger> mLoggers = new ConcurrentHashMap<String, AppLogger>();
	
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

	public AppLogger getLogger(String className) {
		synchronized(mLoggers){
			if (!mLoggers.containsKey(className)){
				mLoggers.put(className, new AppLogger(className));
			}
			return mLoggers.get(className);
		}
	}
	
	public class AppLogger {
		String mTag;
		
		public AppLogger(String tag) {
			this.mTag=tag;
		}
		
		public void addLog(String log){
			//System.out.println(mTag+" : "+log);
		}

	}
	
}
