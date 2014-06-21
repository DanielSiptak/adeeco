package cz.cuni.mff.ms.siptak.adeeco;

import cz.cuni.mff.d3s.events.ChangedKnowledgeEvent;
import cz.cuni.mff.d3s.events.MessageEvent;
import de.greenrobot.event.EventBus;
import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;

public class LogView extends TextView {

	public LogView(Context context) {
		super(context);
		EventBus.getDefault().register(this);
		// TODO Auto-generated constructor stub
	}
	
	public LogView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		EventBus.getDefault().register(this);
		// TODO Auto-generated constructor stub
	}

	public LogView(Context context, AttributeSet attrs) {
		super(context, attrs);
		EventBus.getDefault().register(this);
		// TODO Auto-generated constructor stub
	}

	private void addStringLine(String line){
		append(line+"\n");
        final Layout layout = getLayout();
        if (!isPressed()){
        	if(layout != null){
	            int scrollDelta = layout.getLineBottom(getLineCount() - 1) 
	                - getScrollY() - getHeight();
	            if(scrollDelta > 0)
	                scrollBy(0, scrollDelta);
	        }
        }
    }
	
	public void onEventMainThread(ChangedKnowledgeEvent event){
		//addStringLine("Event: "+event.getKey()+" : "+event.getValue());
	}
	
	public void onEventMainThread(MessageEvent event){
		addStringLine(event.getKey()+" : "+event.getValue());
	}
}



