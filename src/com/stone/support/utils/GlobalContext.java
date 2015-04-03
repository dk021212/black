package com.stone.support.utils;

import java.util.List;

import io.fabric.sdk.android.Fabric;

import com.crashlytics.android.Crashlytics;
import com.stone.bean.AccountBean;
import com.stone.support.crashmanager.CrashManager;
import com.stone.support.crashmanager.CrashManagerConstants;
import com.stone.support.database.AccountDBTask;
import com.stone.support.settinghelper.SettingUtility;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

public class GlobalContext extends Application {

	// singleton
	private static GlobalContext globalContext = null;

	// image size
	private Activity activity = null;

	private Activity currentRunningActivity = null;

	// image memory cache
	private LruCache<String, Bitmap> appBitmapCache = null;

	// current account info
	private AccountBean accountBean = null;

	private Handler handler = new Handler();
	
	public boolean tokenExpiredDialogIsShowing=false;

	@Override
	public void onCreate() {
		super.onCreate();
		globalContext = this;
		buildCache();
		CrashManagerConstants.loadFromContext(this);
		CrashManager.registerHandler();
		if (Utility.isCertificateFingerprintCorrect(this)) {
			Fabric.with(this, new Crashlytics());
		}

	}

	public static GlobalContext getInstance() {
		return globalContext;
	}

	public Handler getUIHandler() {
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
		appBitmapCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount();
			}
		};
	}

	public void setAccountBean(final AccountBean accountBean) {
		this.accountBean = accountBean;
	}

	public AccountBean getAccountBean() {
		if (accountBean == null) {
			String id = SettingUtility.getDefaultAccountId();
			if (!TextUtils.isEmpty(id)) {
				accountBean = AccountDBTask.getAccount(id);
			} else {
				List<AccountBean> accountList = AccountDBTask.getAccountList();
				if (accountList != null && accountList.size() > 0) {
					accountBean = accountList.get(0);
				}
			}
		}

		return accountBean;
	}
}
