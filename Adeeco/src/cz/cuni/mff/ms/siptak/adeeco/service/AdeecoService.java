package cz.cuni.mff.ms.siptak.adeeco.service;

import cz.cuni.mff.d3s.events.BundleEvent;
import cz.cuni.mff.d3s.events.ServiceEvent;
import cz.cuni.mff.d3s.events.ServiceEvent.TYPE;
import cz.cuni.mff.ms.siptak.adeeco.AdeecoActivity;
import cz.cuni.mff.ms.siptak.adeecolib.R;
import de.greenrobot.event.EventBus;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

public class AdeecoService extends Service {
	public static final String PAUSE_RUNTIME = "PAUSE_RUNTIME";
	public static final String BUNDLE_ADD = "ADD_BUNDLE";
	public static final String BUNDLE_REMOVE = "DEL_BUNDLE";

	public static final String MESSENGER = "MESSENGER";
	public static final String SERIALIZED = "SERIALIZED";

	/**
	 * Instance of Adeeco runtime singleton used for this service
	 */
	private AdeecoRuntimeSingleton run;

	/**
	 * For showing and hiding our notification.
	 */
	NotificationManager mNM;

	/**
	 * Boolean determining if activity is connecting to already created service
	 */
	private boolean isStarted=false;
	
	public void onEventBackgroundThread(ServiceEvent event){
		switch (event.getType()) {
		case RUNTIME_START:
			run.startRuntime();
			System.out.println("Deeco runtime started");
			break;
		case RUNTIME_PAUSE:
			run.stopRuntime();
			System.out.println("Deeco runtime paused");
			break;
		default:
			break;			
		}
	}
	
	public void onEventBackgroundThread(BundleEvent event){
		switch (event.getType()) {
		case ADD:
			run.addRuntimeBundle(event.getBundle());
			System.out.println("Deeco runtime " + event.getBundle().getId()+" was added");
			break;
		case REMOVE:
			run.removeRuntimeBoundle(event.getBundleId());
			System.out.println("Deeco runtime " + event.getBundleId()+" was removed");
			break;			
		}
	}
	
	@Override
	public void onCreate() {
		EventBus.getDefault().register(this);
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Display a notification about us starting.
		showNotification();
		run = AdeecoRuntimeSingleton.getInstance();
		run.startRuntime();
	}

	private void postEvent(Object event){
		EventBus.getDefault().post(event);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!isStarted){
			Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
			isStarted=true;
		}
		postEvent(new ServiceEvent(TYPE.SERVICE_STARTED));
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Service done", Toast.LENGTH_SHORT).show();
		// Cancel the persistent notification.
		mNM.cancel(R.string.service_notification);
		run.destroyRuntime();
		postEvent(new ServiceEvent(TYPE.SERVICE_STOPPED));
		EventBus.getDefault().unregister(this);
		stopSelf();
	}
	
	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(getText(R.string.service_notification))
				.setContentText(getText(R.string.service_notification_label));

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, AdeecoActivity.class);
		//resultIntent.addFlags(Intent.FLA);
		// The stack builder object will contain an artificial back stack for
		// the started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(AdeecoActivity.class);
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
