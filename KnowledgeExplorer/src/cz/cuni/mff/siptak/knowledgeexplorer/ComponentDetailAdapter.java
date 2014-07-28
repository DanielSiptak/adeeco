package cz.cuni.mff.siptak.knowledgeexplorer;

import java.util.Calendar;
import java.util.List;

import cz.cuni.mff.ms.dsiptak.knowledgeexplorer.R;
import cz.cuni.mff.siptak.knowledgeexplorer.ComponentContent.ComponentItem.ComponentHolder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ComponentDetailAdapter extends ArrayAdapter<ComponentHolder> {
	private List<ComponentHolder> objects;
	
	public ComponentDetailAdapter(Context context, int resource,
			int textViewResourceId, List<ComponentHolder> objects) {
		super(context, resource, textViewResourceId, objects);
		this.objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		
		TextView text1 = (TextView)view.findViewById(android.R.id.text1);
		TextView text2 = (TextView)view.findViewById(android.R.id.text2);
		TextView sub_text1 = (TextView)view.findViewById(R.id.sub_text1);
		TextView sub_text2 = (TextView)view.findViewById(R.id.sub_text2);
		
		ComponentHolder item = objects.get(position);
		if (item != null) {
			text1.setText(item.id);
			if (item.value.size() > 0 && !item.id.startsWith("#")) {
				if (item.id.equals("id")) {
					text2.setText(item.namespace);//.subSequence(0, 13));// just a begining of namespace as it is too long	
				} else {
					if (item.value.get(0)!=null ) {
						text2.setText(item.value.get(0).toString());
					} else {
						text2.setText(null);
					}
				}
			}
			sub_text1.setText(item.timestamp.get(Calendar.HOUR)+":"+item.timestamp.get(Calendar.MINUTE));
			sub_text2.setText("V:"+item.version);
		}
		
		return view;
	}
}
