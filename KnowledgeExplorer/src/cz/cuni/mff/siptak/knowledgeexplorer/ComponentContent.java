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
public class ComponentContent {
	
	private static final ComponentContent INSTANCE = new ComponentContent();

	@SuppressWarnings("unused")
	private String TAG = "ComponentContent"; 
	
	/**
	 * A list of Knowledge items
	 */
	private List<ComponentItem> item_list = new ArrayList<ComponentItem>();

	/**
	 * A map of Knowledge items, by ID.
	 */
	private Map<String, ComponentItem> knowledge_items = new HashMap<String, ComponentItem>();

	
	public static ComponentContent getInstance() {
		return INSTANCE;
	}

	private ComponentContent() {
		EventBus.getDefault().register(this);
	}
	
	public List<ComponentItem> getItemList() {
		return item_list;
	}
	
	public Map<String, ComponentItem> getItemMap() {
		return knowledge_items;
	}
	
	public void clear(){
		item_list.clear();
		knowledge_items.clear();
		notifyAdapterChanged();
	}
	
	public void onEventMainThread(ChangedKnowledgeEvent event){
		if (!event.getNameSpace().equals("")){
			ComponentItem item = knowledge_items.get(event.getNameSpace());
			if (item == null ) {
				item = new ComponentItem(event.getNameSpace());
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
	public static class ComponentItem {
		private String namespace;
		private String name = null; // this value is taken from ID part of the knowledge 
		private Calendar timestamp = Calendar.getInstance();
		
		class ComponentHolder{
			String id;
			String namespace;
			List<Object> value;
			int version;
			Calendar timestamp;
		}
		
		private List<ComponentHolder> knowledge_list = new ArrayList<ComponentHolder>();

		
		private Map<String,ComponentHolder> knowledge = new HashMap<String,ComponentHolder>();
		
		public ComponentItem(String namespace) {
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
		
		public List<ComponentHolder> getKnowledgeList(){
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
				ComponentHolder holder=knowledge.get(event.getId());
				if (holder==null){
					holder= new ComponentHolder();
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
