package com.stone.ui.interfaces;

import com.stone.support.asyncdrawable.TimeLineBitmapDownloader;
import com.stone.support.settinghelper.SettingUtility;
import com.stone.support.utils.GlobalContext;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

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

	private void reload() {
		Intent intent=getIntent();
		overridePendingTransition(0,0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();
		
		overridePendingTransition(0,0);
		startActivity(intent);
		TimeLineBitmapDownloader.refreshThemePictureBackground();
	}
	
	public TimeLineBitmapDownloader getBitmapDownloader(){
		return TimeLineBitmapDownloader.getInstance();
	}
	
	
}
