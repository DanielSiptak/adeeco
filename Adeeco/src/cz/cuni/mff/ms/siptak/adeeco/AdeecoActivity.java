package cz.cuni.mff.ms.siptak.adeeco;

import java.util.Arrays;
import cz.cuni.mff.d3s.deeco.demo.cloud.AlertEnsemble;
import cz.cuni.mff.d3s.deeco.demo.cloud.MigrationEnsemble;
import cz.cuni.mff.d3s.deeco.demo.cloud.NodeA;
import cz.cuni.mff.d3s.deeco.demo.cloud.NodeB;
import cz.cuni.mff.d3s.events.BundleEvent;
import cz.cuni.mff.d3s.events.ChangedKnowledgeEvent;
import cz.cuni.mff.d3s.events.ServiceEvent;
import cz.cuni.mff.ms.siptak.adeeco.service.AdeecoService;
import cz.cuni.mff.ms.siptak.adeeco.service.RuntimeBundle;
import cz.cuni.mff.ms.siptak.adeecolib.R;
import de.greenrobot.event.EventBus;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.util.Log;
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

	/**
	 * Reference to logView created in this activity
	 */
	LogView logView;
	
	private boolean serviceRunning = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		setContentView(R.layout.activity_adeeco);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("service_autostart", true)) {
			setServiceStatus(true);
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
			//mService = new Messenger(service);
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			Intent intent = new Intent(AdeecoActivity.this, AdeecoService.class);
			stopService(intent);
			//mService = null;
		}
	};

	@Override
	protected void onDestroy() {
		if (isServiceRunning()) {
			unbindService(mConnection);
		}
		super.onDestroy();
	}
	
	private void postEvent(Object event) {
		EventBus.getDefault().post(event);
	}
	
	public void onEvent(ServiceEvent event){
		System.out.println("ServiceEvent received by activity "+event.getType().toString());
		switch (event.getType()){
			case SERVICE_STARTED:
				serviceRunning = true;
				break;
			case SERVICE_STOPPED:
				serviceRunning = false;
				break;
			default:
				break;
		}
	}
	
	public void onEventMainThread(ChangedKnowledgeEvent event){
	//	System.err.println("Event received "+event.getKey()+" : "+event.getValue());
	}
	
	/** SERVICE HANDLING **/
	private boolean isServiceRunning() {
		return serviceRunning;//mService != null && mService.getBinder().isBinderAlive();
	}

	private void setServiceStatus(boolean shouldRun) {
		if (shouldRun && !isServiceRunning()) {
			// we want service running but it is not -> starting
			
			Toast.makeText(this, "Starting service", Toast.LENGTH_SHORT).show();
			final Activity act = this;
			Runnable t = new Runnable() {
				
				@Override
				public void run() {
					Intent intent = new Intent(act, AdeecoService.class);
					startService(intent);
					bindService(new Intent(act, AdeecoService.class), mConnection,
							Context.BIND_AUTO_CREATE);	
					
				}
			};
			new Thread(t).start();
		} else if (!shouldRun && isServiceRunning()) {
			// we want service stopped but it is not -> stopping
			unbindService(mConnection); // we need to unbind so service will not
										// be
			// automatically started.
			Intent intent = new Intent(this, AdeecoService.class);
			stopService(intent);
			//mService = null;
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
				setServiceStatus(!isServiceRunning());
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
		Log.i("Activity","creating menu "+isServiceRunning());
		if (isServiceRunning()) {
			service_item = getText(R.string.service_status_stop);
		} else {
			service_item = getText(R.string.service_status_start);
		}
		menu.findItem(R.id.service_status_change).setTitle(service_item);
		return super.onPrepareOptionsMenu(menu);
	}
	
	public void startRuntime(View view) {
		//sendMessageToService(AppMessenger.SERVICE.RUNTIME_START, null);
		postEvent(new ServiceEvent(ServiceEvent.TYPE.RUNTIME_START));
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
		//EventBus.getDefault().post(new ServiceEvent(ServiceEvent.TYPE.START));
		//sendMessageToService(AppMessenger.SERVICE.RUNTIME_PAUSE, null);
		postEvent(new ServiceEvent(ServiceEvent.TYPE.RUNTIME_PAUSE));
		Button button = (Button) view;
		button.setText(R.string.start_runtime);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startRuntime(v);
			}
		});
	}
/*
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
*/
	public void startBundle(View view) {
		RuntimeBundle bundle = null;
		switch (view.getId()) {
		case R.id.start_nodea:
			bundle = new RuntimeBundle("nodea", 
					Arrays.asList(new Class<?>[] {NodeA.class}),
					Arrays.asList(new Class<?>[] {}));
			break;
		case R.id.start_nodeb:
			bundle = new RuntimeBundle("nodeb", 
					Arrays.asList(new Class<?>[] {NodeB.class }),
					Arrays.asList(new Class<?>[] {}));
			break;
		case R.id.start_migration:
			bundle = new RuntimeBundle("migration", 
					Arrays.asList(new Class<?>[] {}),
					Arrays.asList(new Class<?>[] {MigrationEnsemble.class}));
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
			break;
		}
		if (bundle != null)
			postEvent(new BundleEvent(BundleEvent.TYPE.ADD,bundle));
	}

	public void stopBundle(View view) {
		switch (view.getId()) {
		case R.id.start_migration:
			//sendMessageToService(AppMessenger.SERVICE.BUNDLE_REMOVE, "cloud");
			postEvent(new BundleEvent(BundleEvent.TYPE.REMOVE,"cloud"));
			break;
		case R.id.start_alert:
			//sendMessageToService(AppMessenger.SERVICE.BUNDLE_REMOVE, "convoy");
			postEvent(new BundleEvent(BundleEvent.TYPE.REMOVE,"convoy"));
			break;
		}
	}
}
