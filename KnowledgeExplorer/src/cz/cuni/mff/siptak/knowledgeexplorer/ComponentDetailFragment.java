package cz.cuni.mff.siptak.knowledgeexplorer;

import java.util.List;

import cz.cuni.mff.ms.dsiptak.knowledgeexplorer.R;
import cz.cuni.mff.siptak.knowledgeexplorer.DeecoContent.DeecoItem;
import cz.cuni.mff.siptak.knowledgeexplorer.DeecoContent.DeecoItem.KnowledgeHolder;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A fragment representing a single Component detail screen. This fragment is
 * either contained in a {@link ComponentListActivity} in two-pane mode (on
 * tablets) or a {@link ComponentDetailActivity} on handsets.
 */
public class ComponentDetailFragment extends ListFragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ComponentDetailFragment() {
	}

	private DeecoContent content = DeecoContent.getInstance();
	private DeecoItem item = null;
	private DeecoDetailAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		//content.getItemMap().get();
		List<KnowledgeHolder> list = null;
		if (getArguments().containsKey(ARG_ITEM_ID))
		{
			item = content.getItemMap().get(getArguments().get(ARG_ITEM_ID));
			list = item.getKnowledgeList();
		}
		
		adapter = new DeecoDetailAdapter(getActivity(),
				R.layout.list_detail,
				android.R.id.text1, list);
		
		/*		if (getArguments().containsKey(ARG_ITEM_ID)) {
			mItem = DeecoContent.getInstance().getItemMap().get(getArguments().getString(
					ARG_ITEM_ID));
		}
		*/
		item.registerAdapterObserver(adapter);
		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_component_detail,
				container, false);
		/*
		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.component_detail))
					.setText(mItem.toString());
		}
*/
		return rootView;
	}
	@Override
	public void onDetach() {
		item.unregisterAdapterObserver(adapter);
		super.onDetach();
	}
}
