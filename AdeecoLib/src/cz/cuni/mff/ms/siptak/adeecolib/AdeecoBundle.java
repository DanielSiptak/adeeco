package cz.cuni.mff.ms.siptak.adeecolib;

import cz.cuni.mff.ms.siptak.adeecolib.service.RuntimeBundle;

public abstract class AdeecoBundle {
	
	RuntimeBundle mBundle;
	
	protected abstract void initBundle();
	
	public AdeecoBundle() {
		initBundle();
	}
	
	public RuntimeBundle getRuntimeBundle(){
		return mBundle;
	}
	 
	public String getBundleName(){
		return mBundle.getId();
	}
	
}
