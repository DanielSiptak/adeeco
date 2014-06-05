package cz.cuni.mff.ms.siptak.adeecolib;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.demo.cloud.AlertEnsemble;
import cz.cuni.mff.d3s.deeco.demo.cloud.MigrationEnsemble;
import cz.cuni.mff.d3s.deeco.demo.cloud.NodeA;
import cz.cuni.mff.d3s.deeco.demo.cloud.NodeB;
import cz.cuni.mff.d3s.deeco.demo.convoy.ConvoyEnsemble;
import cz.cuni.mff.d3s.deeco.demo.convoy.RobotFollowerComponent;
import cz.cuni.mff.d3s.deeco.demo.convoy.RobotLeaderComponent;
import cz.cuni.mff.ms.siptak.adeecolib.service.AdeecoService;
import cz.cuni.mff.ms.siptak.adeecolib.service.AppMessenger;
import cz.cuni.mff.ms.siptak.adeecolib.service.AppMessenger.ACTIVITY;
import cz.cuni.mff.ms.siptak.adeecolib.service.RuntimeBundle;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AdeecoActivity extends Activity {
	/**
	 * Multicast lock for enabling multicast for this application
	 */
	MulticastLock lock;
	
	/** Messenger for communicating with service. */
	Messenger mService = null;

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	
	/**
	 * Reference to logView created in this activity
	 */
	LogView logView;
	
	
	/**
	 * Event code used for setting OnEventListener which is receiving all events send to activity.
	 */
	final static int ALL_TAG = Integer.MAX_VALUE;
	
	AppMessenger appMessenger = AppMessenger.getInstance();
	
	/**
	 * Map of listeners on activity side
	 */
	@SuppressLint("UseSparseArrays") 
	Map<Integer, Set<OnEventListener>> mOnEventListeners = new HashMap<Integer, Set<OnEventListener>>();
	
	/**
	 * Handler of incoming messages from service.
	 */
	@SuppressLint("HandlerLeak")
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			
			Set<OnEventListener> listeners = getOnEventListeners(msg.what);
			if (listeners!=null) {
				Bundle bundle = (Bundle) msg.getData();
				for (OnEventListener listener : listeners) {
						listener.onEventAction(bundle);
				}
			} else {
				super.handleMessage(msg);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adeeco);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("service_autostart", true)) {
			setServiceStatus(true);
		}
		
		logView = (LogView) findViewById(R.id.logView1);
		if (logView!=null) {
			addOnEventListener(ACTIVITY.LOG.ordinal(),logView.getOnEventListener());
		}
		if (savedInstanceState!=null) {
			onRestoreInstanceState(savedInstanceState);
		}
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		lock = wifi.createMulticastLock("adeeco");
		lock.setReferenceCounted(true);
		lock.acquire();
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (logView!=null && savedInstanceState!=null) {
			CharSequence seq = savedInstanceState.getCharSequence(getText(R.id.logView1).toString());
			if (seq!=null) {
				logView.setText(seq);	
			}
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (logView!=null){
			CharSequence seq = logView.getText();
			if (seq.length()>0) {
				outState.putCharSequence(getText(R.id.logView1).toString(), seq);
			}
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_options, menu);
		return true;
	}

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.
			mService = new Messenger(service);
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			Intent intent = new Intent(AdeecoActivity.this, AdeecoService.class);
			stopService(intent);
			mService = null;
		}
	};

	@Override
	protected void onDestroy() {
		if (isServiceRunning()) {
			unbindService(mConnection);
		}
		super.onDestroy();
	}

	/** SERVICE HANDLING **/
	private boolean isServiceRunning() {
		return mService != null && mService.getBinder().isBinderAlive();
	}

	private void setServiceStatus(boolean shouldRun) {
		if (shouldRun && !isServiceRunning()) {
			// we want service running but it is not -> starting
			final Activity act = this;
			Thread t = new Thread(){
				public void run(){
					Intent intent = new Intent(act, AdeecoService.class);
					intent.putExtra(AdeecoService.MESSENGER, mMessenger);
					startService(intent);
					bindService(new Intent(act, AdeecoService.class), mConnection,
							Context.BIND_AUTO_CREATE);	
				}
				};
				t.start();
		} else if (!shouldRun && isServiceRunning()) {
			// we want service stopped but it is not -> stopping
			unbindService(mConnection); // we need to unbind so service will not
										// be
			// automatically started.
			Intent intent = new Intent(this, AdeecoService.class);
			stopService(intent);
			mService = null;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		Intent intent;
		switch (item.getItemId()) {
			case R.id.activity_settings:
				intent = new Intent().setClass(this, PreferencesActivity.class);
				break;
			case R.id.service_status_change:
				// changing current service status
				boolean newStatus = !isServiceRunning();
				setServiceStatus(newStatus);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
		startActivity(intent);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		CharSequence service_item;
		if (isServiceRunning()) {
			service_item = getText(R.string.service_status_stop);
		} else {
			service_item = getText(R.string.service_status_start);
		}
		menu.findItem(R.id.service_status_change).setTitle(service_item);
		return super.onPrepareOptionsMenu(menu);
	}
	
	/** EVENT LISTENER */
	public void addOnEventListener(Integer tag, OnEventListener listener) {
		if (!mOnEventListeners.containsKey(tag)) {
			mOnEventListeners.put(tag, new HashSet<OnEventListener>());
		}
		mOnEventListeners.get(tag).add(listener);
	}

	public void setOnEventListener(OnEventListener listener) {
		addOnEventListener(ALL_TAG, listener);
	}

	/**
	 * Returns list of listeners that should be called for @tag
	 * 
	 * @param tag
	 * @return
	 */
	private Set<OnEventListener> getOnEventListeners(Integer tag) {
		Set<OnEventListener> set = new HashSet<OnEventListener>();
		if (mOnEventListeners.containsKey(tag)) {
			for (OnEventListener listener : mOnEventListeners.get(tag)) {
				set.add(listener);
			}	
		}
		if (mOnEventListeners.containsKey(ALL_TAG)) {
			for (OnEventListener listener : mOnEventListeners.get(ALL_TAG)) {
				set.add(listener);
			}
		}
		return set;
	}

	public void removeOnEventListener(OnEventListener listener) {
		for (Set<OnEventListener> set : mOnEventListeners.values()) {
			set.remove(listener);
		}
	}

	public void startRuntime(View view) {
		sendMessageToService(AppMessenger.SERVICE.RUNTIME_START, null);
		Button button = (Button) view;
		button.setText(R.string.pause_runtime);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pauseRuntime(v);
			}
		});
	}

	public void pauseRuntime(View view) {
		sendMessageToService(AppMessenger.SERVICE.RUNTIME_PAUSE, null);
		Button button = (Button) view;
		button.setText(R.string.start_runtime);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startRuntime(v);
			}
		});
	}

	private void sendMessageToService(AppMessenger.SERVICE what, Serializable obj) {
		if (isServiceRunning()) {
			Bundle bundle = new Bundle();
			bundle.putSerializable(AdeecoService.SERIALIZED, obj);
			appMessenger.sendToService(what.ordinal(),bundle);
		} else {
			Toast.makeText(this, "No running service", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void startBundle(View view) {
		RuntimeBundle bundle = null;
		System.out.println("start button presed "+view.getId()+" "+R.id.start_nodea+" "+AppMessenger.SERVICE.BUNDLE_ADD.ordinal());
		switch (view.getId()) {
		case R.id.start_nodea:
			bundle = new RuntimeBundle("nodea", 
					Arrays.asList(new Class<?>[] {NodeA.class}),
					Arrays.asList(new Class<?>[] {}));
			sendMessageToService(AppMessenger.SERVICE.BUNDLE_ADD, bundle);
			break;
		case R.id.start_nodeb:
			bundle = new RuntimeBundle("nodeb", 
					Arrays.asList(new Class<?>[] {NodeB.class }),
					Arrays.asList(new Class<?>[] {}));
			sendMessageToService(AppMessenger.SERVICE.BUNDLE_ADD, bundle);
			break;
		case R.id.start_migration:
			bundle = new RuntimeBundle("migration", 
					Arrays.asList(new Class<?>[] {}),
					Arrays.asList(new Class<?>[] {MigrationEnsemble.class}));
			sendMessageToService(AppMessenger.SERVICE.BUNDLE_ADD, bundle);
			break;			
		case R.id.start_alert:
			/*
			bundle = new RuntimeBundle("convoy",
					Arrays.asList(new Class<?>[] { RobotLeaderComponent.class,
							RobotFollowerComponent.class }),
					Arrays.asList(new Class<?>[] { ConvoyEnsemble.class }));
			*/
			bundle = new RuntimeBundle("alert", 
					Arrays.asList(new Class<?>[] {}),
					Arrays.asList(new Class<?>[] {AlertEnsemble.class }));
			sendMessageToService(AppMessenger.SERVICE.BUNDLE_ADD, bundle);
			break;
		}
	}

	public void stopBundle(View view) {
		switch (view.getId()) {
		case R.id.start_migration:
			sendMessageToService(AppMessenger.SERVICE.BUNDLE_REMOVE, "cloud");
			break;
		case R.id.start_alert:
			sendMessageToService(AppMessenger.SERVICE.BUNDLE_REMOVE, "convoy");
			break;
		}
	}
}
