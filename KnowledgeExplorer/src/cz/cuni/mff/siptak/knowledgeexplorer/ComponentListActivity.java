package cz.cuni.mff.siptak.knowledgeexplorer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.annotations.DEECoComponent;
import cz.cuni.mff.d3s.deeco.annotations.DEECoEnsemble;
import cz.cuni.mff.d3s.events.*;
import cz.cuni.mff.ms.dsiptak.knowledgeexplorer.R;
import cz.cuni.mff.siptak.adeeco.service.AdeecoService;
import cz.cuni.mff.siptak.adeeco.service.RuntimeBundle;
import de.greenrobot.event.EventBus;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * An activity representing a list of Components. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ComponentDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ComponentListFragment} and the item details (if present) is a
 * {@link ComponentDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ComponentListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class ComponentListActivity extends FragmentActivity implements
		ComponentListFragment.Callbacks {

	String TAG = "Activity";
	
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_component_list);
	
		if (findViewById(R.id.component_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((ComponentListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.component_list))
					.setlActivateOnItemClick(true);
		}
		EventBus.getDefault().register(this);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		//Use this to clean preferences if needed
		prefs.edit().clear().commit();
		if (prefs.getBoolean("service_autostart", true)) {
			setServiceStatus(true);
		}
		if (savedInstanceState!=null) {
			onRestoreInstanceState(savedInstanceState);
		}
		/* init content classes so the y register for event handling*/
		ComponentContent.getInstance();
		MessageContent.getInstance();
		
	}

	/**
	 * Callback method from {@link ComponentListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(ComponentDetailFragment.ARG_ITEM_ID, id);
			ComponentDetailFragment fragment = new ComponentDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.component_detail_container, fragment)
					.commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this,
					ComponentDetailActivity.class);
			detailIntent.putExtra(ComponentDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_component, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		Intent intent;
		switch (item.getItemId()) {
			case R.id.activity_message:
				intent = new Intent(this,MessageActivity.class);
				startActivity(intent);
				break;
			case R.id.activity_settings:
				//intent = new Intent().setClass(this, PreferencesActivity.class);
				intent = new Intent(this,PreferencesActivity.class);
				startActivity(intent);
				break;
			case R.id.service_status_change:
				// changing current service status
				setServiceStatus(!isServiceRunning());
			default:
				return super.onOptionsItemSelected(item);
		}
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
	
	private static boolean serviceRunning = false;
	
	/** SERVICE HANDLING **/
	private boolean isServiceRunning() {
		return serviceRunning;//mService != null && mService.getBinder().isBinderAlive();
	}
	
	private void postEvent(Object event) {
		EventBus.getDefault().post(event);
	}
	
	private void addDeecoBundle(){
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Set<String> parts = prefs.getStringSet("deeco_preference",
				new HashSet<String>(
						Arrays.asList(
								getResources().getStringArray(R.array.apps_values)
								)));
		Log.i(TAG,"Going to start these parts : "+parts.toString());
		List<Class<?>> components = new ArrayList<Class<?>>();
		List<Class<?>> ensembles = new ArrayList<Class<?>>();
		for (String part : parts) {
			try {
				Class<?> clazz = Class.forName(part, false, getClassLoader());
				if (clazz.getAnnotation(DEECoComponent.class)!=null) {
					components.add(clazz);
				} else if (clazz.getAnnotation(DEECoEnsemble.class)!=null) {
					ensembles.add(clazz);
				}
			} catch (ClassNotFoundException e) {
				Log.e(TAG,"Can't found class for part "+part);
			}
		}
		if (!(components.isEmpty()&&ensembles.isEmpty())) {			
			RuntimeBundle bundle = new RuntimeBundle(getString(R.string.app_name), components , ensembles);
			postEvent(new BundleEvent(BundleEvent.TYPE.ADD,bundle));
			}
	}

	public void onEventMainThread(ServiceEvent event){
		System.out.println("ServiceEvent received by activity "+event.getType().toString());
		switch (event.getType()){
			case SERVICE_STARTED:
				if ( !isServiceRunning() ) {
					Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
					addDeecoBundle();
				}
				serviceRunning = true;
				break;
			case SERVICE_STOPPED:
				if ( isServiceRunning() ) {
					Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
				}
				serviceRunning = false;
				
				break;
			default:
				break;
		}
	}
	
	private void setServiceStatus(boolean shouldRun) {
		if (shouldRun && !isServiceRunning()) {
			// we want service running but it is not -> starting
			Toast.makeText(this, "Starting service", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this, AdeecoService.class);
			startService(intent);
			//bindService(new Intent(act, AdeecoService.class), mConnection,
			//	Context.BIND_AUTO_CREATE);	
		} else if (!shouldRun && isServiceRunning()) {
			// we want service stopped but it is not -> stopping
			Intent intent = new Intent(this, AdeecoService.class);
			stopService(intent);
			ComponentContent.getInstance().clear();
		}
	}

}
