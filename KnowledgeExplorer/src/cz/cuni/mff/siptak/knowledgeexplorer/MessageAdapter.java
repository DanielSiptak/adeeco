package cz.cuni.mff.siptak.knowledgeexplorer;

import java.util.Calendar;
import java.util.List;

import cz.cuni.mff.siptak.knowledgeexplorer.MessageContent.MessageItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MessageAdapter extends ArrayAdapter<MessageItem> {
	private List<MessageItem> objects;
	
	public MessageAdapter(Context context, int resource,
			int textViewResourceId, List<MessageItem> objects) {
		super(context, resource, textViewResourceId, objects);
		this.objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		
		TextView text1 = (TextView)view.findViewById(android.R.id.text1);
		TextView text2 = (TextView)view.findViewById(android.R.id.text2);
		
		MessageItem item = objects.get(position);
		text1.setText(item.id+" : "+item.message);
		text2.setText(item.timestamp.get(Calendar.HOUR)+":"+item.timestamp.get(Calendar.MINUTE)+" : "+item.timestamp.get(Calendar.SECOND));
		return view;
	}
}
