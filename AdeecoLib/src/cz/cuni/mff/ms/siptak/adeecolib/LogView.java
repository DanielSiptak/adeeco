package cz.cuni.mff.ms.siptak.adeecolib;

import cz.cuni.mff.ms.siptak.adeecolib.service.AppMessenger;
import cz.cuni.mff.ms.siptak.adeecolib.service.AppMessenger.LogLine;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;

public class LogView extends TextView {

	public LogView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public LogView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public LogView(Context context, AttributeSet attrs) {
		super(context, attrs);
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
	
	public OnEventListener getOnEventListener(){
		return new OnEventListener() {
			
			@Override
			public void onEventAction(Bundle bundle) {
				LogLine logLine = (LogLine) bundle.getSerializable(AppMessenger.LOG_STRING);
				if (logLine!=null) {
					addStringLine(logLine.getText());
				}
			}
		};
	}
}



