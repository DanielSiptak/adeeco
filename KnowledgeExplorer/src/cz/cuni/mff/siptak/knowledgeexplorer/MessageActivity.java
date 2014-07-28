package cz.cuni.mff.siptak.knowledgeexplorer;

import cz.cuni.mff.ms.dsiptak.knowledgeexplorer.R;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MessageActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends ListFragment {

		public PlaceholderFragment() {
		}

		//private ArrayAdapter<MessageContent> adapter = null;

		private MessageContent content = MessageContent.getInstance();
		private MessageAdapter adapter;
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			adapter = new MessageAdapter(getActivity(),
					R.layout.list_message,
					android.R.id.text1, content.getList());
			content.registerAdapterObserver(adapter);
			setListAdapter(adapter);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_message,
					container, false);
			return rootView;
		}
		
		@Override
		public void onDetach() {
			content.unregisterAdapterObserver(adapter);
			super.onDetach();
		}
		
	}

}
