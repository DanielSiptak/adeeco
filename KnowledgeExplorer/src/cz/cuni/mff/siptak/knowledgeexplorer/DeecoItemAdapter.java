package cz.cuni.mff.siptak.knowledgeexplorer;

import java.util.Calendar;
import java.util.List;

import cz.cuni.mff.siptak.knowledgeexplorer.DeecoContent.DeecoItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DeecoItemAdapter extends ArrayAdapter<DeecoItem> {
	private List<DeecoItem> objects;
	
	public DeecoItemAdapter(Context context, int resource,
			int textViewResourceId, List<DeecoItem> objects) {
		super(context, resource, textViewResourceId, objects);
		this.objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		
		TextView text2 = (TextView)view.findViewById(android.R.id.text2);
		DeecoItem item = objects.get(position);
		if (item != null) {
			text2.setText(item.getTimestamp().get(Calendar.HOUR)+":"+item.getTimestamp().get(Calendar.MINUTE));
		}
		return view;
	}
}
