package com.stone.support.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.stone.bean.AccountBean;
import com.stone.bean.GroupListBean;
import com.stone.bean.UserBean;
import com.stone.bean.android.MusicInfo;
import com.stone.black.R;
import com.stone.support.crashmanager.CrashManager;
import com.stone.support.crashmanager.CrashManagerConstants;
import com.stone.support.database.AccountDBTask;
import com.stone.support.database.GroupDBTask;
import com.stone.support.settinghelper.SettingUtility;
import com.stone.support.smileypicker.SmileyMap;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;

public class GlobalContext extends Application {

	// singleton
	private static GlobalContext globalContext = null;

	// image size
	private Activity activity = null;

	private Activity currentRunningActivity = null;

	private DisplayMetrics displayMetrics = null;

	// image memory cache
	private LruCache<String, Bitmap> appBitmapCache = null;

	// current account info
	private AccountBean accountBean = null;

	private LinkedHashMap<Integer, LinkedHashMap<String, Bitmap>> emotionsPic = new LinkedHashMap<Integer, LinkedHashMap<String, Bitmap>>();

	private GroupListBean group = null;

	private MusicInfo musicInfo = new MusicInfo();

	private Handler handler = new Handler();

	public boolean tokenExpiredDialogIsShowing = false;

	@Override
	public void onCreate() {
		super.onCreate();
		globalContext = this;
		buildCache();
		CrashManagerConstants.loadFromContext(this);
		CrashManager.registerHandler();
		if (Utility.isCertificateFingerprintCorrect(this)) {
			//Fabric.with(this, new Crashlytics());
		}

	}

	public static GlobalContext getInstance() {
		return globalContext;
	}

	public Handler getUIHandler() {
		return handler;
	}

	public GroupListBean getGroup() {
		if (group == null) {
			group = GroupDBTask.get(GlobalContext.getInstance()
					.getCurrentAccountId());
		}
		return group;
	}

	public void setGroup(GroupListBean group) {
		this.group = group;
	}

	public Activity getCurrentRunningActivity() {
		return currentRunningActivity;
	}

	public void setCurrentRunningActivity(Activity currentRunningActivity) {
		this.currentRunningActivity = currentRunningActivity;
	}

	public String getSpecialToken() {
		if (getAccountBean() != null) {
			return getAccountBean().getAccess_token();
		} else {
			return "";
		}
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

	public synchronized LruCache<String, Bitmap> getBitmapCache() {
		if (appBitmapCache == null) {
			buildCache();
		}

		return appBitmapCache;
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

	public String getCurrentAccountId() {
		return getAccountBean().getUid();
	}

	public String getCurrentAccountName() {

		return getAccountBean().getUsernick();
	}

	public DisplayMetrics getDisplayMetrics() {
		if (displayMetrics != null) {
			return displayMetrics;
		} else {
			Activity a = getActivity();
			if (a != null) {
				Display display = getActivity().getWindowManager()
						.getDefaultDisplay();
				DisplayMetrics metrics = new DisplayMetrics();
				display.getMetrics(metrics);
				this.displayMetrics = metrics;
				return metrics;
			} else {
				// default creen is 800*480
				DisplayMetrics metrics = new DisplayMetrics();
				metrics.widthPixels = 480;
				metrics.heightPixels = 800;
				return metrics;
			}
		}
	}

	public void updateMusicInfo(MusicInfo musicInfo) {
		this.musicInfo = musicInfo;
	}

	public MusicInfo getMusicInfo() {
		return musicInfo;
	}

	public synchronized Map<String, Bitmap> getEmotionsPics() {
		if (emotionsPic != null && emotionsPic.size() > 0) {
			return emotionsPic.get(SmileyMap.GENERAL_EMOTION_POSITION);
		} else {
			getEmotionsTask();
			return emotionsPic.get(SmileyMap.GENERAL_EMOTION_POSITION);
		}
	}

	public synchronized Map<String, Bitmap> getHuahuaPics() {
		if (emotionsPic != null && emotionsPic.size() > 0) {
			return emotionsPic.get(SmileyMap.HUAHUA_EMOTION_POSITION);
		} else {
			getEmotionsTask();
			return emotionsPic.get(SmileyMap.HUAHUA_EMOTION_POSITION);
		}
	}

	private void getEmotionsTask() {
		Map<String, String> general = SmileyMap.getInstance().getGeneral();
		emotionsPic.put(SmileyMap.GENERAL_EMOTION_POSITION,
				getEmotionsTask(general));
		Map<String, String> huahua = SmileyMap.getInstance().getHuahua();
		emotionsPic.put(SmileyMap.HUAHUA_EMOTION_POSITION,
				getEmotionsTask(huahua));
	}

	private LinkedHashMap<String, Bitmap> getEmotionsTask(
			Map<String, String> emotionMap) {
		List<String> index = new ArrayList<String>();
		index.addAll(emotionMap.keySet());
		LinkedHashMap<String, Bitmap> bitmapMap = new LinkedHashMap<String, Bitmap>();
		for (String str : index) {
			String name = emotionMap.get(str);
			AssetManager assetManager = GlobalContext.getInstance().getAssets();
			InputStream inputStream;
			try {
				inputStream = assetManager.open(name);
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				if (bitmap != null) {
					Bitmap scaledBitmap = Bitmap.createScaledBitmap(
							bitmap,
							Utility.dip2px(getResources().getInteger(
									R.integer.emotion_size)),
							Utility.dip2px(getResources().getInteger(
									R.integer.emotion_size)), true);
					if (bitmap != scaledBitmap) {
						bitmap.recycle();
						bitmap = scaledBitmap;
					}

					bitmapMap.put(str, bitmap);
				}
			} catch (IOException ignored) {

			}
		}

		return bitmapMap;
	}

	public static interface MyProfileInfoChangeListener {

		public void onChange(UserBean newUserBean);
	}

	private Set<MyProfileInfoChangeListener> profileListenerSet = new HashSet<MyProfileInfoChangeListener>();

	public void registerForAccountChangeListener(
			MyProfileInfoChangeListener listener) {
		if (listener != null) {
			profileListenerSet.add(listener);
		}
	}

	public void unRegisterForAccountChangeListener(
			MyProfileInfoChangeListener listener) {
		profileListenerSet.remove(listener);
	}
}
