package cz.cuni.mff.siptak.knowledgeexplorer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.d3s.events.ChangedKnowledgeEvent;
import de.greenrobot.event.EventBus;

import android.widget.BaseAdapter;

/**
 * Helper class for providing Knowledge content for user interfaces
 * <p>
 */
public class DeecoContent {
	
	private static final DeecoContent INSTANCE = new DeecoContent();

	@SuppressWarnings("unused")
	private String TAG = "KnowledgeContent"; 
	
	/**
	 * A list of Knowledge items
	 */
	private List<DeecoItem> item_list = new ArrayList<DeecoItem>();

	/**
	 * A map of Knowledge items, by ID.
	 */
	private Map<String, DeecoItem> knowledge_items = new HashMap<String, DeecoItem>();

	
	public static DeecoContent getInstance() {
		return INSTANCE;
	}

	private DeecoContent() {
		EventBus.getDefault().register(this);
	}
	
	public List<DeecoItem> getItemList() {
		return item_list;
	}
	
	public Map<String, DeecoItem> getItemMap() {
		return knowledge_items;
	}
	
	public void clear(){
		item_list.clear();
		knowledge_items.clear();
		notifyAdapterChanged();
	}
	
	public void onEventMainThread(ChangedKnowledgeEvent event){
		if (!event.getNameSpace().equals("")){
			DeecoItem item = knowledge_items.get(event.getNameSpace());
			if (item == null ) {
				item = new DeecoItem(event.getNameSpace());
				item_list.add(item);
			}
			item.aceeptEvent(event);
			knowledge_items.put(event.getNameSpace(), item);
			/*
			if (event.getId().equals("id")) {
				Log.i(TAG, "updateting the adapter");
			}
			*/
			notifyAdapterChanged();
		}
	}
	
	private ArrayList<BaseAdapter> adapters = new ArrayList<BaseAdapter>();

	public void registerAdapterObserver(BaseAdapter observer) {
		adapters.add(observer);
	}
	
	public void unregisterAdapterObserver(BaseAdapter observer){
		adapters.remove(observer);
	}
	
	private void notifyAdapterChanged(){
	    for (BaseAdapter adapter: adapters) {
	        adapter.notifyDataSetChanged();
	    }
	}
	
	/**
	 * A knowledge item representing a piece of content.
	 */
	public static class DeecoItem {
		private String namespace;
		private String name = null; // this value is taken from ID part of the knowledge 
		private Calendar timestamp = Calendar.getInstance();
		
		class KnowledgeHolder{
			String id;
			String namespace;
			List<Object> value;
			int version;
			Calendar timestamp;
		}
		
		private List<KnowledgeHolder> knowledge_list = new ArrayList<KnowledgeHolder>();

		
		private Map<String,KnowledgeHolder> knowledge = new HashMap<String,KnowledgeHolder>();
		
		public DeecoItem(String namespace) {
			this.namespace = namespace;
		}

		@Override
		public String toString() {
			return name;
		}
		
		public String getNameSpace(){
			return namespace;
		}
		
		public String getName(){
			return name;
		}
		
		public Calendar getTimestamp(){
			return timestamp;
		}
		
		public List<KnowledgeHolder> getKnowledgeList(){
			return knowledge_list;
		}
		
		public void aceeptEvent(ChangedKnowledgeEvent event){
			if (namespace.equals(event.getNameSpace())){
				if (event.getId().equals("id")&&event.getValue()!=null){
					Object obj = event.getValue().get(0);
					if (obj !=null) {
						name=obj.toString();
					}
				}
				KnowledgeHolder holder=knowledge.get(event.getId());
				if (holder==null){
					holder= new KnowledgeHolder();
					knowledge_list.add(holder);
				}
				holder.id=event.getId();
				holder.namespace=namespace;
				holder.timestamp = Calendar.getInstance();
				holder.version = event.getVersion();
				holder.value = event.getValue();
				knowledge.put(event.getId(), holder);
				timestamp = Calendar.getInstance();
				notifyAdapterChanged();
			}
		}

		private ArrayList<BaseAdapter> adapters = new ArrayList<BaseAdapter>();

		public void registerAdapterObserver(BaseAdapter observer) {
			adapters.add(observer);
		}
		
		public void unregisterAdapterObserver(BaseAdapter observer){
			adapters.remove(observer);
		}
		
		private void notifyAdapterChanged(){
		    for (BaseAdapter adapter: adapters) {
		        adapter.notifyDataSetChanged();
		    }
		}
	}
}
