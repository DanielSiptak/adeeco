package cz.cuni.mff.ms.siptak.adeecolib.service;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.d3s.deeco.scheduling.ETriggerType;
import cz.cuni.mff.d3s.deeco.scheduling.IKnowledgeChangeListener;
import cz.cuni.mff.ms.siptak.adeecolib.AdeecoActivity;
import cz.cuni.mff.ms.siptak.adeecolib.R;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

public class AdeecoService extends Service {
	public static final String PAUSE_RUNTIME = "PAUSE_RUNTIME";
	public static final String BUNDLE_ADD = "ADD_BUNDLE";
	public static final String BUNDLE_REMOVE = "DEL_BUNDLE";

	public static final String MESSENGER = "MESSENGER";
	public static final String SERIALIZED = "SERIALIZED";

	private static AppMessenger.SERVICE[] msg_types = AppMessenger.SERVICE.values();

	/**
	 * Instance of Adeeco runtime singleton used for this service
	 */
	private AdeecoRuntimeSingleton run;

	/**
	 * Target we publish for activity to send messages to IncomingHandler.
	 */
	private final Messenger mMessenger = new Messenger(new IncomingHandler());

	/**
	 * Keeps track of currently connected activity.
	 */
	private Messenger mActivity;

	/**
	 * For showing and hiding our notification.
	 */
	NotificationManager mNM;

	/**
	 * Boolean determining if activity is connecting to already created service
	 */
	private boolean isStarted=false;
	
	/**
	 * Handler of incoming messages from clients.
	 */
	@SuppressLint("HandlerLeak")
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg_types[msg.what]) {
			case BUNDLE_ADD:
				RuntimeBundle bundle = (RuntimeBundle) msg.getData()
						.getSerializable(SERIALIZED);
				run.addRuntimeBundle(bundle);
				System.out.println("Deeco runtime " + bundle.getId()+" was added");
				break;
			case BUNDLE_REMOVE:
				String id = (String) msg.getData().getSerializable(SERIALIZED);
				run.removeRuntimeBoundle(id);
				System.out.println("Deeco runtime "+id+" removed");
				break;
			case RUNTIME_START:
				run.startRuntime();
				System.out.println("Deeco runtime started");
				break;
			case RUNTIME_PAUSE:
				run.stopRuntime();
				System.out.println("Deeco runtime paused");
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		run = AdeecoRuntimeSingleton.getInstance();
		// Display a notification about us starting.
		showNotification();
		AppMessenger.getInstance().setServiceMessenger(mMessenger);
		run.startRuntime();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			Messenger messenger = (Messenger) intent.getExtras().get(MESSENGER);
			if (messenger != null) {
				mActivity = messenger;
				AppMessenger.getInstance().setActivityMessenger(mActivity);
			}
		}
		if (!isStarted){
			Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
			isStarted=true;
		}
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		mActivity=null;
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Service done", Toast.LENGTH_SHORT).show();
		// Cancel the persistent notification.
		mNM.cancel(R.string.service_notification);
		run.destroyRuntime();
		AppMessenger.getInstance().setActivityMessenger(null);
		AppMessenger.getInstance().setServiceMessenger(null);
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
