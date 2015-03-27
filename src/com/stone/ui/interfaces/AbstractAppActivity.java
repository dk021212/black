package com.stone.ui.interfaces;


import java.lang.reflect.Field;
import com.stone.support.asyncdrawable.TimeLineBitmapDownloader;
import com.stone.support.settinghelper.SettingUtility;
import com.stone.support.utils.GlobalContext;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewConfiguration;

public class AbstractAppActivity extends FragmentActivity {

	protected int theme = 0;

	@Override
	protected void onResume() {
		super.onResume();
		GlobalContext.getInstance().setCurrentRunningActivity(this);

		if (theme != SettingUtility.getAppTheme()) {
			reload();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (GlobalContext.getInstance().getCurrentRunningActivity() == this) {
			GlobalContext.getInstance().setCurrentRunningActivity(null);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("theme", theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			theme = SettingUtility.getAppTheme();
		}else{
			theme=savedInstanceState.getInt("theme");
		}
		
		setTheme(theme);
		
		super.onCreate(savedInstanceState);
		forceShowActionBarOverflowMenu();
		GlobalContext.getInstance().setActivity(this);
	}

	private void forceShowActionBarOverflowMenu() {
		try{
			ViewConfiguration config=ViewConfiguration.get(this);
			Field menuKeyField=ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField!=null){
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config,false);
			}
		}catch(Exception ignored){
			
		}
		
	}

	private void reload() {
		Intent intent = getIntent();
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();

		overridePendingTransition(0, 0);
		startActivity(intent);
		TimeLineBitmapDownloader.refreshThemePictureBackground();
	}

	public TimeLineBitmapDownloader getBitmapDownloader() {
		return TimeLineBitmapDownloader.getInstance();
	}

}
