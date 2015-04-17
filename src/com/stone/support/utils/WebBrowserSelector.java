package com.stone.support.utils;

import com.stone.support.settinghelper.SettingUtility;
import com.stone.ui.browser.BrowserWebActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;

public class WebBrowserSelector {

	public static void openLink(Context context, Uri uri) {
		if (SettingUtility.allowInternalWebBrowser()) {
			Intent intent = new Intent(context, BrowserWebActivity.class);
			intent.putExtra("url", uri.toString());
			context.startActivity(intent);
		} else {
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.putExtra(Browser.EXTRA_APPLICATION_ID,
					context.getPackageName());
			context.startActivity(intent);
		}
	}

}
