package cz.cuni.mff.siptak.adeeco.service;

import cz.cuni.mff.d3s.events.BundleEvent;
import cz.cuni.mff.d3s.events.ServiceEvent;
import cz.cuni.mff.d3s.events.ServiceEvent.TYPE;
import cz.cuni.mff.ms.dsiptak.knowledgeexplorer.R;
import cz.cuni.mff.siptak.knowledgeexplorer.ComponentListActivity;

import de.greenrobot.event.EventBus;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class AdeecoService extends Service {
	/**
	 * Instance of Adeeco runtime singleton used for this service
	 */
	private AdeecoRuntimeSingleton run;

	/**
	 * For showing and hiding our notification.
	 */
	NotificationManager mNM;

	/**
	 * Multicast lock for enabling multicast for this application
	 */
	MulticastLock lock;

	public void onEventBackgroundThread(ServiceEvent event){
		switch (event.getType()) {
		case RUNTIME_START:
			run.startRuntime();
			Log.i("SERVICE","Deeco runtime started");
			break;
		case RUNTIME_PAUSE:
			run.stopRuntime();
			Log.i("SERVICE","Deeco runtime paused");
			break;
		default:
			break;			
		}
	}
	
	public void onEventBackgroundThread(BundleEvent event){
		switch (event.getType()) {
		case ADD:
			run.addRuntimeBundle(event.getBundle());
			Log.i("SERVICE","Deeco runtime " + event.getBundle().getId()+" was added");
			break;
		case REMOVE:
			run.removeRuntimeBoundle(event.getBundleId());
			Log.i("SERVICE","Deeco runtime " + event.getBundleId()+" was removed");
			break;
		}
	}
	
	@Override
	public void onCreate() {
		EventBus.getDefault().register(this);
		Runnable r = new Runnable() {
            public void run() {
            	mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        		// Display a notification about us starting.
        		showNotification();
        		WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        		lock = wifi.createMulticastLock(getString(R.string.app_name));
        		lock.setReferenceCounted(true);
        		lock.acquire();
        		run = AdeecoRuntimeSingleton.getInstance();
        		run.startRuntime();
        		postEvent(new ServiceEvent(TYPE.SERVICE_STARTED));
            }
        };
        new Thread(r).start();
	}

	private void postEvent(Object event){
		EventBus.getDefault().post(event);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		mNM.cancel(R.string.service_notification);
		run.destroyRuntime();
		postEvent(new ServiceEvent(TYPE.SERVICE_STOPPED));
		EventBus.getDefault().unregister(this);
		lock.release();
		stopSelf();
	}
	
	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.deeco_icon)
				.setContentTitle(getText(R.string.service_notification))
				.setContentText(getText(R.string.service_notification_label));

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, ComponentListActivity.class);
		// The stack builder object will contain an artificial back stack for
		// the started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(ComponentListActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		// mId allows you to update the notification later on.

		Notification notification = mBuilder.build();
		// We use a string id because it is a unique number. We use it later to
		// cancel or update.
		mNM.notify(R.string.service_notification, notification);
		startForeground(R.string.service_notification, notification);
	}
}
