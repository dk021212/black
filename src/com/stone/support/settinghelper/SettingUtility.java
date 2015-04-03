package com.stone.support.settinghelper;

import android.content.Context;

import com.stone.black.R;
import com.stone.support.utils.GlobalContext;
import com.stone.ui.preference.SettingActivity;

public class SettingUtility {

	private static Context getContext() {
		return GlobalContext.getInstance();
	}

	public static int getAppTheme() {
		String value = SettingHelper.getSharedPreferences(getContext(),
				SettingActivity.THEME, "1");
		switch (Integer.valueOf(value)) {
		case 1:
			return R.style.AppTheme_Light;
		case 2:
			return R.style.AppTheme_Dark;
		default:
			return R.style.AppTheme_Light;
		}
	}

	public static String getDefaultAccountId() {
		return SettingHelper.getSharedPreferences(getContext(), "id", "");
	}
}
