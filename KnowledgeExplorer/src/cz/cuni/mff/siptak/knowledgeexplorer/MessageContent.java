package cz.cuni.mff.siptak.knowledgeexplorer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.widget.BaseAdapter;

import cz.cuni.mff.d3s.events.MessageEvent;
import de.greenrobot.event.EventBus;

public class MessageContent {

	private static final MessageContent INSTANCE = new MessageContent();

	@SuppressWarnings("unused")
	private String TAG = "MessageContent"; 
	
	/**
	 * A list of Knowledge items
	 */
	private LinkedList<MessageItem> list = new LinkedList<MessageItem>();

	public static MessageContent getInstance() {
		return INSTANCE;
	}

	private MessageContent() {
		EventBus.getDefault().register(this);
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
	
	public List<MessageItem> getList(){
		return list;
	}
	
	public void onEventMainThread(MessageEvent event){
		list.addFirst(new MessageItem(event));
		while (list.size()>100) {
			list.pollLast();
		}
		notifyAdapterChanged();
	}
	
	class MessageItem{
		String id;
		String message;
		Calendar timestamp;
		
		MessageItem(MessageEvent event){
			this.id = event.getKey();
			this.message = event.getValue();
			this.timestamp = Calendar.getInstance();
		}
	}
}
