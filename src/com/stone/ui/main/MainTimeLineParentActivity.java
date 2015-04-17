package com.stone.ui.main;

import java.lang.reflect.Field;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.stone.support.asyncdrawable.TimeLineBitmapDownloader;
import com.stone.support.error.WeiboException;
import com.stone.support.settinghelper.SettingUtility;
import com.stone.support.utils.GlobalContext;

public class MainTimeLineParentActivity extends SlidingFragmentActivity {

	private int theme = 0;

	@Override
	protected void onResume() {
		super.onResume();

		GlobalContext.getInstance().setCurrentRunningActivity(this);

		if (theme == SettingUtility.getAppTheme()) {

		} else {
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
	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			theme = SettingUtility.getAppTheme();
		} else {
			theme = savedInstanceState.getInt("theme");
		}

		setTheme(theme);
		super.onCreate(savedInstanceState);
		forceShowActionBarOverflowMenu();
		GlobalContext.getInstance().setActivity(this);
		TimeLineBitmapDownloader.refreshThemePictureBackground();
	}

	private void forceShowActionBarOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ignred) {

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void reload() {
		Intent intent = getIntent();
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();

		overridePendingTransition(0, 0);
		startActivity(intent);
	}

	protected void dealWithException(WeiboException e) {
		Toast.makeText(this, e.getError(), Toast.LENGTH_LONG).show();
	}

}
