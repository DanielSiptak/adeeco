package cz.cuni.mff.siptak.knowledgeexplorer;

import java.util.Calendar;
import java.util.List;

import cz.cuni.mff.siptak.knowledgeexplorer.ComponentContent.ComponentItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ComponenttemAdapter extends ArrayAdapter<ComponentItem> {
	private List<ComponentItem> objects;
	
	public ComponenttemAdapter(Context context, int resource,
			int textViewResourceId, List<ComponentItem> objects) {
		super(context, resource, textViewResourceId, objects);
		this.objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		
		TextView text2 = (TextView)view.findViewById(android.R.id.text2);
		ComponentItem item = objects.get(position);
		if (item != null) {
			text2.setText(item.getTimestamp().get(Calendar.HOUR)+":"+item.getTimestamp().get(Calendar.MINUTE));
		}
		return view;
	}
}
