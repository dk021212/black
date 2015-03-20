package com.stone.support.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class GlobalContext extends Application {

	// singleton
	private static GlobalContext globalContext = null;
	
	//image size
	private Activity activity=null;
	
	private Activity currentRunningActivity = null;

	public static GlobalContext getInstance() {
		return globalContext;
	}

	public Activity getCurrentRunningActivity() {
		return currentRunningActivity;
	}

	public void setCurrentRunningActivity(Activity currentRunningActivity) {
		this.currentRunningActivity = currentRunningActivity;
	}

	public Activity getActivity() {
		
		return activity;
	}
	
	public void setActivity(Activity activity){
		this.activity=activity;
	}

}
