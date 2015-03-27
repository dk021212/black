package com.stone.support.utils;

import io.fabric.sdk.android.Fabric;

import com.crashlytics.android.Crashlytics;
import com.stone.support.crashmanager.CrashManager;
import com.stone.support.crashmanager.CrashManagerConstants;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.util.LruCache;

public class GlobalContext extends Application {

	// singleton
	private static GlobalContext globalContext = null;

	// image size
	private Activity activity = null;

	private Activity currentRunningActivity = null;

	// image memory cache
	private LruCache<String, Bitmap> appBitmapCache = null;
	
	private Handler handler=new Handler();

	@Override
	public void onCreate() {
		super.onCreate();
		globalContext = this;
		buildCache();
		CrashManagerConstants.loadFromContext(this);
		CrashManager.registerHandler();
		if(Utility.isCertificateFingerprintCorrect(this)){
			Fabric.with(this, new Crashlytics());
		}
		
	}

	public static GlobalContext getInstance() {
		return globalContext;
	}
	
	public Handler getUIHandler(){
		return handler;
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

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	private void buildCache() {
		int memClass = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
				.getMemoryClass();
		int cacheSize = Math.max(1024 * 1024 * 8, 1024 * 1024 * memClass / 5);
		appBitmapCache=new LruCache<String,Bitmap>(cacheSize){
			@Override
			protected int sizeOf(String key,Bitmap bitmap){
				return bitmap.getByteCount();
			}
		};
	}
}
